package cn.kuboard.webhook.user;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

public class KuboardWebhookUser {

  @NotNull
  @Schema(description = "用户名")
  private String username;

  @Schema(description = "用户全名", requiredMode = RequiredMode.NOT_REQUIRED)
  private String fullName;

  @Schema(description = "用户邮箱地址", requiredMode = RequiredMode.NOT_REQUIRED)
  private String email;

  @Schema(description = "用户所属分组", requiredMode = RequiredMode.NOT_REQUIRED)
  private List<String> groups;

  @Schema(description = "用户创建时间", requiredMode = RequiredMode.NOT_REQUIRED)
  private Date createTime;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<String> getGroups() {
    return groups;
  }

  public void setGroups(List<String> groups) {
    this.groups = groups;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

}
