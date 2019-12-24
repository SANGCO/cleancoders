package function;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

public class FitnessExample {
    public String testableHtml(PageData pageData, boolean includeSuiteSetup) throws Exception {
        return new TestableHtmlMaker(pageData, includeSuiteSetup).invoke();
    }

    private class TestableHtmlMaker {
        private PageData pageData;
        private boolean includeSuiteSetup;

        public TestableHtmlMaker(PageData pageData, boolean includeSuiteSetup) {
            this.pageData = pageData;
            this.includeSuiteSetup = includeSuiteSetup;
        }

        public String invoke() throws Exception {
            WikiPage wikiPage = pageData.getWikiPage();
            StringBuffer buffer = new StringBuffer();

            if (pageData.hasAttribute("Test")) {
                if (includeSuiteSetup) {
                    WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_SETUP_NAME, wikiPage);
                    if (suiteSetup != null) {
                        String mode = "setup";
                        includePage(wikiPage, buffer, suiteSetup, mode);
                    }
                }
                WikiPage setup = PageCrawlerImpl.getInheritedPage("SetUp", wikiPage);
                if (setup != null) {
                    String mode = "setup";
                    includePage(wikiPage, buffer, setup, mode);
                }
            }

            buffer.append(pageData.getContent());
            if (pageData.hasAttribute("Test")) {
                WikiPage teardown = PageCrawlerImpl.getInheritedPage("TearDown", wikiPage);
                if (teardown != null) {
                    String mode = "teardown";
                    includePage(wikiPage, buffer, teardown, mode);
                }
                if (includeSuiteSetup) {
                    WikiPage suiteTeardown = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_TEARDOWN_NAME, wikiPage);
                    if (suiteTeardown != null) {
                        String mode = "teardown";
                        includePage(wikiPage, buffer, suiteTeardown, mode);
                    }
                }
            }

            pageData.setContent(buffer.toString());
            return pageData.getHtml();
        }

        private void includePage(WikiPage wikiPage, StringBuffer buffer, WikiPage suiteSetup, String mode) throws Exception {
            WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(suiteSetup);
            String pagePathName = PathParser.render(pagePath);
            buffer.append("!include -" + mode + " .").append(pagePathName).append("\n");
        }
    }
}
