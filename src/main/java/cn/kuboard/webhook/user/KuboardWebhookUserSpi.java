package cn.kuboard.webhook.user;

import cn.kuboard.webhook.common.KuboardWebhookPage;
import cn.kuboard.webhook.common.KuboardWebhookResponse;

public interface KuboardWebhookUserSpi {

  public KuboardWebhookResponse<Void> authenticate(KuboardWebhookAuthenticateRequest request);

  public KuboardWebhookResponse<KuboardWebhookPage<KuboardWebhookUser>> list(int pageNum, int pageSize,
      String username);

  public KuboardWebhookResponse<KuboardWebhookUser> findByUsername(String username);

}
