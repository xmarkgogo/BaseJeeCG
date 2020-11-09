package org.jeecg.modules.elfinder.util;
import org.jeecg.modules.elfinder.service.FsItem;
import org.jeecg.modules.elfinder.service.FsService;

import java.io.IOException;

/**
 * 从资源表中，获取权限资源
 * 锁定
 * 可读
 * 可编辑(删除，更新)
 */
public class FsSecurityCheckUtils {


    public boolean isLocked(FsService fsService, FsItem fsi) throws IOException {
        System.out.println(fsService.getHash(fsi));
        return true;
    }

    public boolean isReadable(FsService fsService, FsItem fsi) throws IOException {
        System.out.println(fsService.getHash(fsi));
        return true;

    }

    public boolean isWritable(FsService fsService, FsItem fsi) throws IOException {
        System.out.println(fsService.getHash(fsi));
        return true;
    }

}
