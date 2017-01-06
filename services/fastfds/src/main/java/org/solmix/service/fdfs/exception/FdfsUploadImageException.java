package org.solmix.service.fdfs.exception;

/**
 * 上传图片例外
 * 
 * 
 *
 */
public class FdfsUploadImageException extends FdfsException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    protected FdfsUploadImageException(String message) {
        super(message);
    }

    public FdfsUploadImageException(String message, Throwable cause) {
        super(message, cause);
    }

}
