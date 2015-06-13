package com.automata.expt.crawl

import spock.lang.Specification

/**
 * Created by Amith Krishnan on 6/12/15.
 */
class CrawlerSpec extends Specification {

    Crawler crawler

    def setup(){
        crawler = new Crawler()
    }

    def "crawl fetches the HTML content corresponding to provided url"(){
        setup:
        String url = "http://google.com/"

        when:
        String output = crawler.crawl(url)

        then:
        output
    }

}
