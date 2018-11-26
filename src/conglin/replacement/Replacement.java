package conglin.replacement;

import java.util.regex.Matcher;

/**
 * 一个构造字符串的规范接口
 */
public interface Replacement {
    /**
     * 用于构造字符串
     * @param matcher 一个匹配器
     * @return 返回构造的String字符串
     */
    String replacementString(Matcher matcher);
}
