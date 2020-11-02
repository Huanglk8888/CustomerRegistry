package com.hlk.dfs.namenode.server;

import java.util.LinkedList;
import java.util.List;

/**
 * 负责管理edits log日志的核心组件
 *
 * @author huanglk
 */
public class FSEditlog {

    /**
     * 当前递增到的txid的序号
     */
    private long txidSeq = 0L;
    /**
     * 内存双缓冲区
     */
    private DoubleBuffer editLogBuffer = new DoubleBuffer();


    /**
     * 记录edits log日志
     *
     * @param content
     */
    public void logEdit(String content) {
        // 这里必须得直接加锁
        synchronized (this) {
            // 获取全局唯一递增的txid，代表了edits log的序号
            txidSeq++;
            long txid = txidSeq;

            // 构造一条edits log对象
            EditLog log = new EditLog(txid, content);
            // 将edits log写入内存缓冲中，不是直接刷入磁盘文件
            editLogBuffer.write(log);
        }
    }

    /**
     * 代表了一条edits log
     *
     * @author zhonghuashishan
     */
    class EditLog {

        long txid;
        String content;

        public EditLog(long txid, String content) {
            this.txid = txid;
            this.content = content;
        }

    }

    /**
     * 内存双缓冲
     *
     * @author zhonghuashishan
     */
    class DoubleBuffer {
        /**
         * 是专门用来承载线程写入edits log
         */
        List<EditLog> currentBuffer = new LinkedList<>();
        /**
         * 专门用来将数据同步到磁盘中去的一块缓冲
         */
        List<EditLog> syncBuffer = new LinkedList<EditLog>();

        /**
         * 将edits log写到内存缓冲里去
         *
         * @param log
         */
        public void write(EditLog log) {
            currentBuffer.add(log);
        }

        /**
         * 交换两块缓冲区，为了同步内存数据到磁盘做准备
         */
        public void setReadyToSync() {
            List<EditLog> tmp = currentBuffer;
            currentBuffer = syncBuffer;
            syncBuffer = tmp;
        }

        /**
         * 将syncBuffer缓冲区中的数据刷入磁盘中
         */
        public void flush() {
            for (EditLog log : syncBuffer) {
                System.out.println("将edit log写入磁盘文件中：" + log);
                // 正常来说，就是用文件输出流将数据写入磁盘文件中
            }
            syncBuffer.clear();
        }

    }

}
