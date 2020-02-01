package com.github.houbb.segment.test.support.segment;

import com.github.houbb.segment.support.segment.strategy.impl.hmm.Viterbi;
import org.junit.Test;

/**
 * @author binbin.hou
 * @since 1.0.0
 */
public class ViterbiTest {

    @Test
    public void commonTest() {
        // BMES
        //[2, 0, 1, 2, 0, 2, 0]
        final String text = "南京市长江大桥";

        System.out.println(Viterbi.segment(text));
    }

}