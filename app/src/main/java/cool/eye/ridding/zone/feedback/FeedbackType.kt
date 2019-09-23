package cn.sudiyi.app.client.account.support

enum class FeedbackType constructor(val type: Int, val value: String) {

    FUNC(1, "功能意见"), PAGE(2, "页面意见"), DEMAND(3, "您的新需求"), OPERATION(4, "操作意见"),
    FLOW(5, "流量问题"), OTHER(6, "其他")
}