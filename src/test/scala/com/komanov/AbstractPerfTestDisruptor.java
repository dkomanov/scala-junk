package com.komanov;

public abstract class AbstractPerfTestDisruptor
{
    public static final int RUNS = 10;
    private static final long ITERATIONS = 1000L * 1000L * 200L;

    protected void testImplementations()
            throws Exception
    {
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        if (getRequiredProcessorCount() > availableProcessors)
        {
            System.out.print("*** Warning ***: your system has insufficient processors to execute the test efficiently. ");
            System.out.println("Processors required = " + getRequiredProcessorCount() + " available = " + availableProcessors);
        }

        long[] disruptorOps = new long[RUNS];

        System.out.println("Starting performance tests");
        for (int i = 0; i < RUNS; i++)
        {
            System.gc();
            disruptorOps[i] = runDisruptorPass();
            System.out.format("Run %d, Parse UUID=%,d ops/sec%n", i, Long.valueOf(disruptorOps[i]));
        }
    }

    public static void printResults(final String className, final long[] disruptorOps, final long[] queueOps)
    {
        for (int i = 0; i < RUNS; i++)
        {
            System.out.format("%s run %d: BlockingQueue=%,d Disruptor=%,d ops/sec\n",
                    className, Integer.valueOf(i), Long.valueOf(queueOps[i]), Long.valueOf(disruptorOps[i]));
        }
    }

    protected abstract int getRequiredProcessorCount();

    protected long runDisruptorPass() throws InterruptedException
    {
        long start = System.currentTimeMillis();
        for (long i = 0; i < ITERATIONS; i++) {
            runAlgorithm();
        }
        long end = System.currentTimeMillis();

        long opsPerSec = ITERATIONS / (end - start);
        return opsPerSec;
    }


    protected abstract void runAlgorithm();
}
