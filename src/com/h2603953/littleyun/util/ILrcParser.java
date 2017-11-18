package com.h2603953.littleyun.util;

import java.util.List;

import com.h2603953.littleyun.bean.LrcBean;

public interface ILrcParser {

    List<LrcBean> getLrcRows(String str);
}
