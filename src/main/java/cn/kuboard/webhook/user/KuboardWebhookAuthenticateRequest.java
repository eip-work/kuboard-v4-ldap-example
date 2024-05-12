package cn.kuboard.webhook.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class KuboardWebhookAuthenticateRequest {

  @NotNull
  @Schema(description = "用户名")
  private String username;

  @NotNull
  @Schema(description = "用户密码 - 明文")
  private String password;

  public KuboardWebhookAuthenticateRequest() {
  };

  public KuboardWebhookAuthenticateRequest(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
