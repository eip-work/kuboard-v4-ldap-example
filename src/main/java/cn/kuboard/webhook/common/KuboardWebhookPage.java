package cn.kuboard.webhook.common;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class KuboardWebhookPage<T> {

  @Schema(description = "数据列表")
  private List<T> list;

  @Schema(description = "分页号")
  private long pageNum;

  @Schema(description = "分页大小")
  private long pageSize;

  @Schema(description = "记录总数")
  private long total;

  public KuboardWebhookPage() {
    this.list = new ArrayList<>();
  }

  public KuboardWebhookPage(long pageNum, long pageSize, long total, List<T> list) {
    this.pageNum = pageNum;
    this.pageSize = pageSize;
    this.total = total;
    this.list = list;
  }

  public List<T> getList() {
    return list;
  }

  public void setList(List<T> list) {
    this.list = list;
  }

  public long getPageNum() {
    return pageNum;
  }

  public void setPageNum(long pageNum) {
    this.pageNum = pageNum;
  }

  public long getPageSize() {
    return pageSize;
  }

  public void setPageSize(long pageSize) {
    this.pageSize = pageSize;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

}