package cn.kuboard.webhook.common;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

public class KuboardWebhookResponse<T> {

  public static final int OK = 0;
  public static final int AUTHENTICATION_FAILED = 1;
  public static final int USER_NOT_FOUND = 2;
  public static final int INTERNAL_SERVER_ERROR = 3;

  @NotNull
  @Schema(description = """
      返回状态码，列表如下：
      * 0 - 成功
      * 1 - 用户认证失败
      * 2 - 用户未找到
      * 3 - 服务器内部错误
      """, requiredMode = RequiredMode.REQUIRED)
  private int code;

  @Schema(description = "错误原因", requiredMode = RequiredMode.NOT_REQUIRED)
  private String message;

  @Schema(description = "错误的详细描述", requiredMode = RequiredMode.NOT_REQUIRED)
  private String details;

  @Schema(description = "返回消息的数据内容", requiredMode = RequiredMode.NOT_REQUIRED)
  private T data;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public static <K> KuboardWebhookResponse<K> success(K data) {
    KuboardWebhookResponse<K> r = new KuboardWebhookResponse<>();
    r.code = OK;
    r.message = "ok";
    r.data = data;
    return r;
  }

  public static <K> KuboardWebhookResponse<K> error(int code, String message, String details) {
    KuboardWebhookResponse<K> r = new KuboardWebhookResponse<>();
    r.code = code;
    r.message = message;
    r.details = details;
    return r;
  }

}
