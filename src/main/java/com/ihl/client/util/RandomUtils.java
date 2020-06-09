//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ihl.client.util;

import java.util.Random;

public final class RandomUtils {
    public RandomUtils() {
    }

    public static int nextInt(int startInclusive, int endExclusive) {
        return startInclusive != endExclusive && endExclusive - startInclusive > 0 ? startInclusive + getRandom().nextInt(endExclusive - startInclusive) : startInclusive;
    }

    public static double nextDouble(double startInclusive, double endInclusive) {
        return startInclusive != endInclusive && endInclusive - startInclusive > 0.0D ? startInclusive + (endInclusive - startInclusive) * Math.random() : startInclusive;
    }

    public static float nextFloat(float startInclusive, float endInclusive) {
        return startInclusive != endInclusive && endInclusive - startInclusive > 0.0F ? (float)((double)startInclusive + (double)(endInclusive - startInclusive) * Math.random()) : startInclusive;
    }

    public static String randomNumber(int length) {
        return random(length, "123456789");
    }

    public static String randomString(int length) {
        return random(length, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    public static String random(int length, String chars) {
        return random(length, chars.toCharArray());
    }

    public static String random(int length, char[] chars) {
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < length; ++i) {
            stringBuilder.append(chars[getRandom().nextInt(chars.length)]);
        }

        return stringBuilder.toString();
    }

    public static Random getRandom() {
        return new Random();
    }
}
