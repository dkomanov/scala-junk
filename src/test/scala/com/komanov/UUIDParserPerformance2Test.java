package com.komanov;

import com.komanov.uuid.UuidJavaFinalUtils2;

public class UUIDParserPerformance2Test extends AbstractPerfTestDisruptor {


    private static final String TestUUID = "01234567-89ab-cdef-ABCD-EF1234567890";

    @Override
    protected int getRequiredProcessorCount() {
        return 1;
    }


    @Override
    protected void runAlgorithm() {
        UuidJavaFinalUtils2.fromStringFast(TestUUID);
    }
}
