package cn.kuboard.example.user.repository.controller;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.kuboard.webhook.common.KuboardWebhookPage;
import cn.kuboard.webhook.common.KuboardWebhookResponse;
import cn.kuboard.webhook.user.KuboardWebhookAuthenticateRequest;
import cn.kuboard.webhook.user.KuboardWebhookUser;
import cn.kuboard.webhook.user.KuboardWebhookUserSpi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping(value = "/api/kuboard/example/ldap-users", produces = "application/json; charset=UTF-8")
@Tag(name = "KuboardWebhookUserSpi", description = """
  Kuboard 通过此 webhook 可以使用外部的用户信息进行认证。
  * 关于用户信息：
    * 如果 Kuboard 内建用户库与外部用户库中存在同名用户，将优先使用 Kuboard 内建用户库中的用户信息；
    * Kuboard 不会对外部用户库中的信息进行缓存或者同步，如果此服务出现故障，将导致对应的用户无法登录 Kuboard；
  * 关于用户组：
    * 可以使用外部用户库中的用户-用户组绑定信息（需要在 Kuboard 中存在同名用户组）；
    * 也可以在 Kuboard 中为用户额外配置用户组；
  * 关于角色：
    * 角色信息需在 Kuboard 中配置；
    * 用户组与角色的绑定信息需在 Kuboard 中配置；
   """)
public class LdapUserController implements KuboardWebhookUserSpi {

  @Autowired
  private LdapTemplate ldapTemplate;
  
  @Operation(summary = "用户认证", description = """
      通过用户名、密码进行认证
      """)
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = """
        Http 状态码为 200 时，代表此接口正常执行。认证是否成功，需要通过响应报文的 code 字段做进一步判断。具体参考该字段的描述。
        """)
  })
  @PostMapping("")
  public KuboardWebhookResponse<Void> authenticate(@RequestBody KuboardWebhookAuthenticateRequest authenticateRequest) {
    String username = authenticateRequest.getUsername();
    String password = authenticateRequest.getPassword();
    log.info("authenticate username: {}", username);
    LdapQuery query = LdapQueryBuilder.query().filter("(&(objectClass=inetOrgPerson)(uid=" + username + "))");
    try {
      ldapTemplate.authenticate(query, password);
      log.info("user {} login success.", username);
      return KuboardWebhookResponse.success(null);
    } catch (AuthenticationException e) {
      log.error("user {} login failed. {} - {}", username, e.getClass().getSigners(), e.getMessage());
      return KuboardWebhookResponse.error(KuboardWebhookResponse.AUTHENTICATION_FAILED, "AuthenticationException", e.getMessage());
    } catch (UncategorizedLdapException e) {
      log.error("user {} login failed. {} - {}", username, e.getClass().getSigners(), e.getMessage());
      return KuboardWebhookResponse.error(KuboardWebhookResponse.INTERNAL_SERVER_ERROR, "UncategorizedLdapException", e.getMessage());
    } catch (NamingException e) {
      log.error("user {} login failed. {} - {}", username, e.getClass().getSigners(), e.getMessage());
      return KuboardWebhookResponse.error(KuboardWebhookResponse.INTERNAL_SERVER_ERROR, "NamingException", e.getMessage());
    } catch (EmptyResultDataAccessException e) {
      log.error("user {} login failed. {} - {}", username, e.getClass().getSigners(), e.getMessage());
      return KuboardWebhookResponse.error(KuboardWebhookResponse.USER_NOT_FOUND, "EmptyResultDataAccessException", e.getMessage());
    }
  }

  @Operation(summary = "查询用户列表", description = """
    在 Kuboard 用户列表界面查询 webhook 用户库的用户列表信息时，将调用此接口。
    """)
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = """
        Http 状态码为 200 时，代表此接口正常执行。认证是否成功，需要通过响应报文的 code 字段做进一步判断。具体参考该字段的描述。
        """)
  })
  @GetMapping("")
  public KuboardWebhookResponse<KuboardWebhookPage<KuboardWebhookUser>> list(
    @Parameter(description = "当前分页") @RequestParam(value = "pageNum", defaultValue = "1") @Min(1) int pageNum, 
    @Parameter(description = "分页大小") @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) int pageSize, 
    @Parameter(description = "用户名，实现时，建议支持模糊查询") @RequestParam(value = "username", required = false, defaultValue = "") String username) {

      log.info("list user, pageNum: {}, pageSize: {}, username: {}", pageNum, pageSize, username);
      // LDAP 的查询方法不能实现分页，此处仅通过 pageSize 限制最大返回集，不能针对 pageNum 做出响应。
      // 对应的负面影响是，在 Kuboard 的用户列表界面上，翻页按钮是无效的。
      LdapQuery query = null;
      if (username.length() > 0) {
        query = LdapQueryBuilder.query().countLimit(pageSize).filter("(&(objectClass=inetOrgPerson)(uid=*" + username + "*))");
      } else {
        query = LdapQueryBuilder.query().countLimit(pageSize).filter("(objectClass=inetOrgPerson)");
      }

  
      List<KuboardWebhookUser> userList;
      try {
        userList = ldapTemplate.search(query, new KuboardWebhookUserAttributeMapper());
        KuboardWebhookPage<KuboardWebhookUser> result = new KuboardWebhookPage<>();
        result.setList(userList);
        result.setPageSize(pageSize);
        result.setPageNum(pageNum);
        result.setTotal(userList.size());
        log.info("count: {}", userList.size());
        return KuboardWebhookResponse.success(result);
      } catch (Exception e) {
        log.error("list user failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        return KuboardWebhookResponse.error(KuboardWebhookResponse.INTERNAL_SERVER_ERROR, e.getClass().getSimpleName(), e.getMessage());
      }
  }

  @Operation(summary = "获取用户详细信息", description = """
    Kuboard 界面完成登录时，将调用此接口获取用户的详细信息
    """)
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = """
        Http 状态码为 200 时，代表此接口正常执行。认证是否成功，需要通过响应报文的 code 字段做进一步判断。具体参考该字段的描述。
        """)
  })
  @GetMapping("/{username}")
  public KuboardWebhookResponse<KuboardWebhookUser> findByUsername(
    @Parameter(description = "用户名") @PathVariable(name = "username") String username) {
    
    log.info("findByUsername {}", username);
    // application.yaml 中设置了 spring.ldap.base 为 ou=users,dc=kuboard,dc=cn
    // 所以，此处 lookup 的第一个参数只需要设置 cn=username 即可 
    try {
      KuboardWebhookUser user = ldapTemplate.lookup("cn=" + username, new KuboardWebhookUserAttributeMapper());
      return KuboardWebhookResponse.success(user);
    } catch(NamingException e) {
      log.error("findByUsername failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
      return KuboardWebhookResponse.error(KuboardWebhookResponse.USER_NOT_FOUND, e.getClass().getSimpleName(), e.getMessage());
    } catch (Exception e) {
      log.error("findByUsername failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
      return KuboardWebhookResponse.error(KuboardWebhookResponse.INTERNAL_SERVER_ERROR, e.getClass().getSimpleName(), e.getMessage());
    }
    
  }

    class KuboardWebhookUserAttributeMapper implements AttributesMapper<KuboardWebhookUser> {

    @Override
    public KuboardWebhookUser mapFromAttributes(Attributes attributes) throws NamingException, javax.naming.NamingException {

      KuboardWebhookUser user = new KuboardWebhookUser();
      NamingEnumeration<String> enumeration = attributes.getIDs();
      while (enumeration.hasMoreElements()) {
        String key = enumeration.nextElement();
        if (key.equals("uid")) {
          user.setUsername(attributes.get(key).get(0).toString());
        }
        if (key.equals("cn")) {
          user.setFullName(attributes.get(key).get(0).toString());
        }
        // 您还可以根据您 LDAP 中的信息，调用 user.setEmail(email);  user.setCreateTime(date); 两个方法。
      }

      // 在你实际的代码中，应该根据 LDAP 中的数据判断用户属于哪个分组，此处仅为演示的目的，将所有用户都添加到 test-group 分组，并额外设置 group1 和 group2
      List<String> groups = new ArrayList<>();
      groups.add("test-group");
      if (user.getUsername().endsWith("1")) {
        groups.add("group1");
      }
      if (user.getUsername().endsWith("2")) {
        groups.add("group2");
      }
      user.setGroups(groups);
      return user;
    }
  }

  
}
