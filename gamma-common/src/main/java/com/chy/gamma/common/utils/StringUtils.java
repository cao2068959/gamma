package com.chy.gamma.common.utils;


import java.util.Arrays;

public class StringUtils {

    public static String toClassPath(String prefix, String suffix, String data) {
        if (isEmpty(data)) {
            return null;
        }

        SmartCharArray suffixSc = new SmartCharArray(suffix);

        char[] chars = data.toCharArray();

        boolean macthSuffix = true;
        for (int i = chars.length - suffixSc.length() - 1; i >=0 && i < chars.length; i++) {
            char c = chars[i];
            macthSuffix = suffixSc.compare(c);
        }
        if (!macthSuffix) {
            return null;
        }


        boolean matchPrefix = false;
        SmartCharArray prefixSc = new SmartCharArray(prefix);
        if (prefixSc.length() == 0){
            matchPrefix = true;
        }

        SmartCharArray result = new SmartCharArray(chars.length - suffixSc.length() - prefixSc.length());
        if(result.length() == 0){
            return null;
        }

        for (int i = 0; i < chars.length - suffixSc.length()  ; i++) {
            if(i > prefixSc.length() && !matchPrefix){
                break;
            }

            char c = chars[i];
            if (!matchPrefix) {
                matchPrefix = prefixSc.compare(c);
            } else {
                if (c == '/' || c == '\\'){
                    c = '.';
                }
                if(!result.append(c)){
                    throw new RuntimeException("格式化失败："+ data);
                }
            }
        }
        if(!result.isAppend()){
            return null;
        }


        return result.toString();
    }


    public static boolean isEmpty(String data) {
        if (data == null || data.length() == 0) {
            return true;
        }
        return false;
    }

    static class SmartCharArray {
        char[] chars;
        int compareIndex = 0;
        int index = 0;
        boolean isAppendData = false;

        SmartCharArray(char[] chars) {
            this.chars = chars;
        }

        SmartCharArray(int len) {
            if (len < 0){
                len = 0;
            }
            this.chars = new char[len];
        }

        SmartCharArray(String data) {

            if (data == null || data.length() == 0) {
                this.chars = new char[0];
            } else {
                this.chars = data.toCharArray();
            }
        }

        boolean isAppend(){
            return isAppendData;
        }

        boolean append(char c) {
            isAppendData = true;
            //越界了
            if (index >= chars.length) {
                return false;
            }
            chars[index] = c;
            index++;
            return true;
        }

        /**
         * 拼接 String 越界了会自动扩容
         *
         * @param value
         * @return
         */
        public boolean append(String value) {
            char[] valueChars = value.toCharArray();
            for (char valueChar : valueChars) {
                if (!append(valueChar)) {
                    //扩容
                    dilatancy(valueChars.length);
                    append(valueChar);
                }
            }
            return true;
        }


        /**
         * 连续比较，连续传入 的char 和对象内持有的 chars 比较
         *
         * @param value
         * @return
         */
        public boolean compare(char value) {
            if (value != chars[compareIndex]) {
                compareIndex = 0;
                return false;
            }

            if (compareIndex == chars.length - 1) {
                return true;
            }

            compareIndex++;
            return false;
        }

        /**
         * 扩容
         *
         * @param num 要扩容的大小
         */
        public void dilatancy(int num) {
            int newlen = chars.length + num;
            chars = Arrays.copyOf(chars, newlen);
        }

        public void clean() {
            compareIndex = 0;
            index = 0;
        }

        public void cleanlast(int num) {
            index = index - num;
        }

        public int length() {
            return chars.length;
        }

        @Override
        public String toString() {
            return String.valueOf(Arrays.copyOf(chars, index));
        }

    }

}
