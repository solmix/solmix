package org.solmix.service.fdfs.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缩略图配置测试
 * 
 * @author tobato
 *
 */
public class DefaultThumbImageConfigTest {

    /** 日志 */
    protected static Logger LOGGER = LoggerFactory.getLogger(DefaultThumbImageConfigTest.class);

    private ThumbImageConfig thumbImageConfig=new DefaultThumbImageConfig(150,150);

    @Test
    public void testGetThumbImagePrefixName() {
        assertNotNull(thumbImageConfig.getPrefixName());
    }

    @Test
    public void testGetThumbImagePath() {

        String path = "wKgBaVaNODiAPpVCAAGtJ7UVNRA438.jpg";
        String thumbPath = "wKgBaVaNODiAPpVCAAGtJ7UVNRA438" + thumbImageConfig.getPrefixName() + ".jpg";

        String result = thumbImageConfig.getThumbImagePath(path);
        LOGGER.debug(result);
        assertEquals(thumbPath, result);

    }

}
