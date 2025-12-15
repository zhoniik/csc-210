package com.tests.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.main.math.BooleanLogic;

public class BooleanLogicTest {

    @Test
    public void testAnd() {
        assertTrue(BooleanLogic.And(true, true));
    }

    @Test
    public void testOr() {
        assertTrue(BooleanLogic.Or(true, false));
    }

    @Test
    public void testNot() {
        assertTrue(BooleanLogic.Not(false));
    }

    @Test
    public void testXor() {
        assertTrue(BooleanLogic.Xor(true, false));
    }
}
