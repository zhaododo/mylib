package com.android.dream.app.widget;

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

//import com.android.mms.R;



import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.dream.app.R;

/**
 * A class for annotating a CharSequence with spans to convert textual emoticons
 * to graphical ones.
 */
public class SmileyParser {
    // Singleton stuff

    private static SmileyParser sInstance;
    public static SmileyParser getInstance() { return sInstance; }
    public static void init(Context context) {
        sInstance = new SmileyParser(context);
    }

    private final Context mContext;
    private final String[] mSmileyTexts;
    private final Pattern mPattern;
    private final HashMap<String, Integer> mSmileyToRes;

    private SmileyParser(Context context) {
        mContext = context;
        mSmileyTexts = mContext.getResources().getStringArray(DEFAULT_SMILEY_TEXTS);
        mSmileyToRes = buildSmileyToRes();
        mPattern = buildPattern();
    }
    
    static class Smileys {
    	//����ͼƬ����
        private static final int[] sIconIds = {
            R.drawable.aini,
            R.drawable.aoteman,
            R.drawable.baibai,
            R.drawable.baobao,
            R.drawable.beiju,
            R.drawable.beishang,
            R.drawable.bianbian,
            R.drawable.bishi,
            R.drawable.bizui,
            R.drawable.buyao,
            R.drawable.chanzui, 
            R.drawable.good
        };
        //��ͼƬӳ��Ϊ ����
        public static int aini = 0;
        public static int aoteman = 1;
        public static int baibai = 2;
        public static int baobao = 3;
        public static int beiju = 4;
        public static int beishang = 5;
        public static int bianbian = 6;
        public static int bishi = 7;
        public static int bizui = 8;
        public static int buyao = 9;
        public static int chanzui = 10;
        public static int good = 11;
 
 
        //�õ�ͼƬ���� ���id
        public static int getSmileyResource(int which) {
            return sIconIds[which];
        }
    }

    // NOTE: if you change anything about this array, you must make the corresponding change

    // to the string arrays: default_smiley_texts and default_smiley_names in res/values/arrays.xml

    public static final int[] DEFAULT_SMILEY_RES_IDS = {
        Smileys.getSmileyResource(Smileys.aini), // 0
        Smileys.getSmileyResource(Smileys.aoteman), // 1
        Smileys.getSmileyResource(Smileys.baibai), // 2
        Smileys.getSmileyResource(Smileys.baobao), // 3
        Smileys.getSmileyResource(Smileys.beiju), // 4
        Smileys.getSmileyResource(Smileys.beishang), // 5
        Smileys.getSmileyResource(Smileys.bianbian), // 6
        Smileys.getSmileyResource(Smileys.bishi), // 7
        Smileys.getSmileyResource(Smileys.bizui), // 8
        Smileys.getSmileyResource(Smileys.buyao), // 9
        Smileys.getSmileyResource(Smileys.chanzui), // 10
        Smileys.getSmileyResource(Smileys.good), // 11


    };

    public static final int DEFAULT_SMILEY_TEXTS = R.array.default_smiley_texts;
    public static final int DEFAULT_SMILEY_NAMES = R.array.default_smiley_names;

    /**
     * Builds the hashtable we use for mapping the string version
     * of a smiley (e.g. ":-)") to a resource ID for the icon version.
     */
    private HashMap<String, Integer> buildSmileyToRes() {
        if (DEFAULT_SMILEY_RES_IDS.length != mSmileyTexts.length) {
            // Throw an exception if someone updated DEFAULT_SMILEY_RES_IDS

            // and failed to update arrays.xml

            throw new IllegalStateException("Smiley resource ID/text mismatch");
        }

        HashMap<String, Integer> smileyToRes =
                            new HashMap<String, Integer>(mSmileyTexts.length);
        for (int i = 0; i < mSmileyTexts.length; i++) {
            smileyToRes.put(mSmileyTexts[i], DEFAULT_SMILEY_RES_IDS[i]);
        }

        return smileyToRes;
    }

    
    /**
     * Builds the regular expression we use to find smileys in {@link #addSmileySpans}.
     */
    //����������ʽ
    private Pattern buildPattern() {
        // Set the StringBuilder capacity with the assumption that the average

        // smiley is 3 characters long.

        StringBuilder patternString = new StringBuilder(mSmileyTexts.length * 3);

        // Build a regex that looks like (:-)|:-(|...), but escaping the smilies

        // properly so they will be interpreted literally by the regex matcher.

        patternString.append('(');
        for (String s : mSmileyTexts) {
            patternString.append(Pattern.quote(s));
            patternString.append('|');
        }
        // Replace the extra '|' with a ')'

        patternString.replace(patternString.length() - 1, patternString.length(), ")");

        return Pattern.compile(patternString.toString());
    }


    /**
     * Adds ImageSpans to a CharSequence that replace textual emoticons such
     * as :-) with a graphical version.
     *
     * @param text A CharSequence possibly containing emoticons
     * @return A CharSequence annotated with ImageSpans covering any
     * recognized emoticons.
     */
    //����ı��滻��ͼƬ
    public CharSequence addSmileySpans(CharSequence text) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Matcher matcher = mPattern.matcher(text);
        while (matcher.find()) {
            int resId = mSmileyToRes.get(matcher.group());
            builder.setSpan(new ImageSpan(mContext, resId),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }
}



