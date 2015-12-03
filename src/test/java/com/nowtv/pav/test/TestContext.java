package com.nowtv.pav.test;


import com.nowtv.pav.test.steps.VoucherManagerStepdefs;

public class TestContext {

    private static final InheritableThreadLocal<TestContext> testContext = new InheritableThreadLocal<TestContext>() {
        @Override
        protected TestContext initialValue() {
            return new TestContext();
        }

        @Override
        protected TestContext childValue(TestContext parentValue) {
            return parentValue.copy();
        }
    };

    private TestContext copy() {
        TestContext childContext = new TestContext();
        childContext.accountId = this.accountId;
        childContext.campaignId = this.campaignId;
        childContext.voucherCode = this.voucherCode;
        childContext.promoCode = this.promoCode;
        childContext.campaignName = this.campaignName;
        childContext.voucherBatchId = this.voucherBatchId;
        childContext.priceHash = this.priceHash;
        return childContext;
    }

    public static TestContext testContext() {
        return testContext.get();
    }

    public Long campaignId;
    public long accountId = VoucherManagerStepdefs.pickAccountId();
    public String voucherCode;
    public String promoCode;
    public String campaignName;
    public String voucherBatchId;
    public String priceHash;
}
