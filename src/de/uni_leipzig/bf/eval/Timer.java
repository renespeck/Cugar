/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uni_leipzig.bf.eval;

import java.lang.management.*;

/**
 *
 * @author ngonga
 */
public class Timer {

    /** Get CPU time in nanoseconds. */
    public static long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
    }

    /** Get user time in nanoseconds. */
    public static long getUserTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadUserTime() : 0L;
    }

    /** Get system time in nanoseconds. */
    public static long getSystemTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? (bean.getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime()) : 0L;
    }

    public static void main(String args[]) {
        long startSystemTimeNano = getSystemTime();
        long startUserTimeNano = getUserTime();

        //System.out.println("User time:" + startSystemTimeNano + "\tSystem time:" + startUserTimeNano);        

        long taskUserTimeNano = getUserTime() - startUserTimeNano;
        long taskSystemTimeNano = getSystemTime() - startSystemTimeNano;
        System.out.println("User time:" + taskUserTimeNano/1000000 + "\tSystem time:" + taskSystemTimeNano);
    }
}
