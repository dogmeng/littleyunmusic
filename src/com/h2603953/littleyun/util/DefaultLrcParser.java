package com.h2603953.littleyun.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.h2603953.littleyun.bean.LrcBean;



public class DefaultLrcParser implements ILrcParser {
    private static final DefaultLrcParser istance = new DefaultLrcParser();

    public static final DefaultLrcParser getIstance() {
        return istance;
    }

    private DefaultLrcParser() {
    }


    @Override
    public List<LrcBean> getLrcRows(String str) {

        if (TextUtils.isEmpty(str)) {
            return null;
        }
        BufferedReader br = new BufferedReader(new StringReader(str));

        List<LrcBean> lrcRows = new ArrayList<LrcBean>();
        String lrcLine;
        try {
            while ((lrcLine = br.readLine()) != null) {
                List<LrcBean> rows = LrcBean.praseRows(lrcLine);
                if (rows != null && rows.size() > 0) {
                    lrcRows.addAll(rows);
                }
            }
            Collections.sort(lrcRows);
            int len = lrcRows.size();
//            做卡拉OK时或许用到
//            for (int i = 0; i < len - 1; i++) {
//                lrcRows.get(i).setTotalTime(lrcRows.get(i + 1).getTime() - lrcRows.get(i).getTime());
//            }
//            lrcRows.get(len - 1).setTotalTime(5000);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return lrcRows;
    }

}
