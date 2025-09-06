package com.hashsoft.audiotape

import timber.log.Timber

class TimberLine : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format(
            "%s,%s:%s",
            super.createStackElementTag(element),
            element.fileName,
            element.lineNumber
        )
    }

    /**
     * 指定された優先度、タグ、メッセージ、およびスロー可能オブジェクトでメッセージをログに記録します。
     *
     * このメソッドは、タグが「tag,filename:lineNumber」の形式である場合、
     * ログメッセージにファイル名と行番号を含めるようにデフォルトのログ記録動作をオーバーライドします。
     * それ以外の場合は、デフォルトのログ記録動作にフォールバックします。
     *
     * @param priority ログメッセージの優先度。
     * @param tag ログメッセージのタグ。
     * @param message ログに記録するメッセージ。
     * @param t ログに記録するスロー可能オブジェクト。スロー可能オブジェクトがない場合は null。
     */
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val logs = tag?.split(",");
        if (logs?.size != 2) {
            super.log(priority, tag, message, t)
        } else {
            super.log(priority, logs[0], message + " (" + logs[1] + ")", t)
        }
    }
}