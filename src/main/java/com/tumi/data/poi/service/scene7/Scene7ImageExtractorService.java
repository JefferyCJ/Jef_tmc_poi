package com.tumi.data.poi.service.scene7;

import com.googlecode.easyec.sika.WorkData;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/14 1:57 PM
 * @describe:
 */
public interface Scene7ImageExtractorService {
    /**
     * 同步图片
     * @param list
     */
    @Async
    void executeSyncPicture(List<WorkData> list);
}

