package com.yida.spider.huxiu.pojo;

public class HuxiuPagingResponse {
	private String data;
	private String last_dateline;
	private String msg;
	private String result;
	private String total_page;
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getLast_dateline() {
		return last_dateline;
	}
	public void setLast_dateline(String last_dateline) {
		this.last_dateline = last_dateline;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getTotal_page() {
		return total_page;
	}
	public void setTotal_page(String total_page) {
		this.total_page = total_page;
	}
	@Override
	public String toString() {
		return "HuxiuPagingResponse [data=" + data + ", last_dateline=" + last_dateline + ", msg=" + msg + ", result="
				+ result + ", total_page=" + total_page + "]";
	}
}
