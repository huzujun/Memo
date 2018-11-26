package conglin.markdown;

import conglin.link.LinkDefinition;
import conglin.replacement.Replacement;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  该类用于处理markdown文本
 */

public class MarkdownProcessor {
    private MarkdownContentEditor markdownContentEditor;
    private int listLevel;
    private Map<String, LinkDefinition> linkDefinitions = new TreeMap<String, LinkDefinition>();

    /**
     * 由String构造生成对象
     * @param markdownContent String类型字符串构造MarkdownProcessor对象
     */
    public MarkdownProcessor(String markdownContent) {
        this.listLevel = 0;
        if(markdownContent == null){
            markdownContentEditor = new MarkdownContentEditor("");
        }else{
            markdownContentEditor = new MarkdownContentEditor(markdownContent);
        }
    }

    /**
     * 由StringBuffer构造生成对象
     * @param markdownContent StringBuffer类型字符串构造MarkdownProcessor对象
     */
    public MarkdownProcessor(StringBuffer markdownContent) {
        this.listLevel = 0;
        if(markdownContent == null){
            markdownContentEditor = new MarkdownContentEditor("");
        }else{
            markdownContentEditor = new MarkdownContentEditor(markdownContent);
        }
    }

    /**
     * 转化的主方法
     * @return 返回一个已经处理好的String对象
     */
    public String translate(){
        markdownContentEditor = formatContent(markdownContentEditor);
        markdownContentEditor = stripLinkDefinitions(markdownContentEditor);

        markdownContentEditor = runBlockItem(markdownContentEditor);

        return markdownContentEditor.toString();
    }

    /**
     * 处理块元素，例如标题、分割线、列表、斜体、加粗、删除线、标记等
     * @param _markdownContentEditor MarkdownContentEditor对象
     * @return 返回一个已经处理标题、分割线、列表、斜体、加粗、删除线、标记的MarkdownContentEditor对象
     */
    public MarkdownContentEditor runBlockItem(MarkdownContentEditor _markdownContentEditor){
        _markdownContentEditor = doHeaders(_markdownContentEditor);      //标题
        _markdownContentEditor = doDividingLine(_markdownContentEditor); //分割线
        _markdownContentEditor = doLists(_markdownContentEditor);        //列表
        _markdownContentEditor = doItalicsAndBoldAndDeletelineAndSign(_markdownContentEditor);//斜体、加粗、删除线、标记

        return formParagraphs(_markdownContentEditor);
    }

    /**
     * 处理跨区域元素，例如图片、链接等
     * @param _markdownContentEditor MarkdownContentEditor对象
     * @return 返回一个已经处理跨区域元素，例如图片、链接等的MarkdownContentEditor对象
     */
    public MarkdownContentEditor runSpanItem(MarkdownContentEditor _markdownContentEditor){
        _markdownContentEditor = doImages(_markdownContentEditor);//图片
        _markdownContentEditor = doReferencesLinks(_markdownContentEditor);//参考式链接
        _markdownContentEditor = doInlineLinks(_markdownContentEditor);//内部式链接
        _markdownContentEditor = doAutoLinks(_markdownContentEditor);//自动链接
        _markdownContentEditor = encodeAmpsAndAngles(_markdownContentEditor);//处理 < 和 &

        _markdownContentEditor.replaceAll(" {2,}\n", " <br />\n");//加入换行符
        return _markdownContentEditor;
    }

    /**
     * 格式化内容，将换行符统一为\n, \t转为四个空格
     * @param _markdownContentEditor MarkdownContentEditor对象
     * @return 返回一个已经格式化处理的MarkdownContentEditor对象
     */
    private MarkdownContentEditor formatContent(MarkdownContentEditor _markdownContentEditor){
        _markdownContentEditor.replaceAll("\\r\\n", "\n"); 	// DOS to Unix
        _markdownContentEditor.replaceAll("\\r", "\n");    	// Mac to Unix
        _markdownContentEditor = _markdownContentEditor.tabToSpaces();
        return _markdownContentEditor;
    }

    /**
     * 处理段落
     * @param _markdownContentEditor
     * @return 返回一个已经处理段落的MarkdownContentEditor对象
     */
    private MarkdownContentEditor formParagraphs(MarkdownContentEditor _markdownContentEditor){
        //去除字符串首尾多余的 \n
        _markdownContentEditor.deleteAll("\\A\\n+");
        _markdownContentEditor.deleteAll("\\n+\\z");

        MarkdownContentEditor markdownContentEditorArray[] = MarkdownContentEditor.split(_markdownContentEditor ,"\\n{2,}");
        for(int i = 0; i < markdownContentEditorArray.length; i++){
            markdownContentEditorArray[i] = runSpanItem(markdownContentEditorArray[i]);
            markdownContentEditorArray[i].prepend("<p>").append("</p>");//加入<p>  </p>
        }
        return MarkdownContentEditor.join(markdownContentEditorArray, "\n\n");
    }

    /**
     * 斜体、加粗、删除线、标记区段处理
     * @param _markdownContentEditor MarkdownContentEditor对象
     * @return 返回一个已经处理斜体、加粗、删除线、标记区段的MarkdownContentEditor对象
     */
    private MarkdownContentEditor doItalicsAndBoldAndDeletelineAndSign(MarkdownContentEditor _markdownContentEditor){
        _markdownContentEditor.replaceAll("(\\*\\*|__)(?=\\S)(.+?[*_]*)(?<=\\S)\\1", "<strong>$2</strong>");
        _markdownContentEditor.replaceAll("(\\*|_)(?=\\S)(.+?)(?<=\\S)\\1", "<em>$2</em>");
        _markdownContentEditor.replaceAll("(\\~\\~)(?=\\S)(.+?[*_]*)(?<=\\S)\\1","<s>$2</s>");
        _markdownContentEditor.replaceAll("(\\`)(?=\\S)(.+?)(?<=\\S)\\1", "<code>$2</code>");
        return _markdownContentEditor;
    }

    /**
     * 处理标题
     * @param _markdownContentEditor MarkdownContentEditor对象
     * @return 返回一个已经处理标题的MarkdownContentEditor对象
     */
    private MarkdownContentEditor doHeaders (MarkdownContentEditor _markdownContentEditor){
        // Setext 风格的标题
        //类 Setext 形式是用底线的形式，利用 = （最高阶标题）和 - （第二阶标题）
        _markdownContentEditor.replaceAll("^(.*)\n====+$", "<h1>$1</h1>");
        _markdownContentEditor.replaceAll("^(.*)\n----+$", "<h2>$1</h2>");

        // Atx 风格的标题
        //类 Atx 形式则是在行首插入 1 到 6 个 # ，对应到标题 1 到 6 阶
        Pattern p = Pattern.compile("^(#{1,6})\\s*(.*?)\\s*\\1?$", Pattern.MULTILINE);
        _markdownContentEditor.replaceAll(p, new Replacement() {
            public String replacementString(Matcher m) {
                String marker = m.group(1);
                String heading = m.group(2);
                int level = marker.length();
                String tag = "h" + level;
                return "<" + tag + ">" + heading + "</" + tag + ">\n";
            }
        });
        return _markdownContentEditor;
    }

    /**
     * 处理分割线
     * @param _markdownContentEditor MarkdownContentEditor对象
     * @return 返回一个已经处理分割线的MarkdownContentEditor对象
     */
    private MarkdownContentEditor doDividingLine(MarkdownContentEditor _markdownContentEditor){
        String[] hrDelimiters = {"\\*", "-", "_"};
        for (String hrDelimiter : hrDelimiters) {
            //_markdownContentEditor.replaceAll("^[ ]{0,2}([ ]?" + hrDelimiter + "[ ]?){3,}[ ]*$", "<hr />");
            _markdownContentEditor.replaceAll("^[ ]{0,2}([ ]?" + hrDelimiter + "[ ]?){3,}[ ]*$",
            "<HR style=\"FILTER: progid:DXImageTransform.Microsoft.Shadow(color:#987cb9,direction:145,strength:15)\" width=\"80%\" color=#987cb9 SIZE=1>");
        }
        return  _markdownContentEditor;
    }

    /**
     * 处理特殊符号
     * @param _markdownContentEditor MarkdownContentEditor对象
     * @return 返回一个已经处理特殊符号的MarkdownContentEditor对象
     */
    private MarkdownContentEditor encodeAmpsAndAngles(MarkdownContentEditor _markdownContentEditor){
        //处理 & 和 <
        _markdownContentEditor.replaceAll("&(?!#?[xX]?(?:[0-9a-fA-F]+|\\w+);)", "&amp;");
        _markdownContentEditor.replaceAll("<(?![a-zA-Z/?\\$!])", "&lt;");
        return _markdownContentEditor;
    }

    /**
     * 将链接加入linkDefinitions
     * @param _markdownContentEditor MarkdownContentEditor对象
     * @return 将链接加入Map中，返回处理后的MarkdownContentEditor对象
     */
    private MarkdownContentEditor stripLinkDefinitions(MarkdownContentEditor _markdownContentEditor) {
        Pattern pattern = Pattern.compile("^[ ]{0,3}\\[(.+)\\]:" + // $1 是ID
                        "[ \\t]*\\n?[ \\t]*" +                            // 空白
                        "<?(\\S+?)>?" +                                     // $2 是 URL
                        "[ \\t]*\\n?[ \\t]*" +                             // 空白
                        "(?:[\"(](.+?)[\")][ \\t]*)?" +                     // $3 是可选标题
                        "(?:\\n+|\\Z)",
                Pattern.MULTILINE);

        _markdownContentEditor.replaceAll(pattern, new Replacement() {
            @Override
            public String replacementString(Matcher matcher) {
                String id = matcher.group(1).toLowerCase();
                String url = encodeAmpsAndAngles(new MarkdownContentEditor(matcher.group(2))).toString();
                String title = matcher.group(3);

                if (title == null) {
                    title = "";
                }
                title = title.replaceAll("\"", "&quot;");//引号转为 &quot;
                linkDefinitions.put(id, new LinkDefinition(url, title));
                return "";
            }
        });
        return _markdownContentEditor;
    }

    /**
     * 自动跳转链接(网址和邮箱)
     * @param _markdownContentEditor MarkdownContentEditor对象
     * @return 返回一个已经处理自动跳转链接的MarkdownContentEditor对象
     */
    private MarkdownContentEditor doAutoLinks(MarkdownContentEditor _markdownContentEditor) {
        //例如 <https://CongLinDev.github.io> 会出现蓝色链接
        _markdownContentEditor.replaceAll("<((http?|https?|ftp):[^'\">\\s]+)>",
                                            "<a href=\"$1\">$1</a>");
        //例如 <conglindev@live.com> 会出现蓝色链接
        _markdownContentEditor.replaceAll("<([-.\\w]+\\@[-a-z0-9]+(\\.[-a-z0-9]+)*\\.[a-z]+)>",
                                         "<a href=\"mailto:$1\">$1</a>");
        return _markdownContentEditor;
    }

    /**
     * 处理参考式链接
     * @param _markdownContentEditor MarkdownContentEditor对象
     * @return 返回一个已经处理参考式链接的MarkdownContentEditor对象
     */
    private MarkdownContentEditor doReferencesLinks(MarkdownContentEditor _markdownContentEditor){
        /*
         * 例如：
         *    [从林的Github][1]
         *    [从林的Github博客主页][2]
         *    [1]:https://github.com/CongLinDev
         *    [2]:https:://CongLinDev.github.io "这是一个可选的标题"
         */
        Pattern referencesLink = Pattern.compile("(" +
                "\\[(.*?)\\]" + // 链接文本 是 $2
                "[ ]?(?:\\n[ ]*)?" +
                "\\[(.*?)\\]" + // ID 是 $3
                ")");
        _markdownContentEditor.replaceAll(referencesLink, new Replacement() {
            @Override
            public String replacementString(Matcher matcher) {
                String replacementText;
                String matchedAllContent = matcher.group(1);
                String linkContent = matcher.group(2);
                String id = matcher.group(3).toLowerCase();

                if(id.equals("")){      //处理[matchedContent][]情况
                    id = linkContent.toLowerCase();
                }

                LinkDefinition tempLinkDefinition = linkDefinitions.get(id);
                if(tempLinkDefinition != null){
                    String url = tempLinkDefinition.getUrl();
                    String title = tempLinkDefinition.getTitle();
                    String titleTag = "";
                    if(title != null && !title.equals("")){
                        titleTag = " title=\"" + title + "\"";
                    }
                    replacementText= "<a href=\"" + url + "\"" + titleTag + ">" + linkContent + "</a>";
                }else {
                    replacementText = matchedAllContent;
                }
                return replacementText;
            }
        });
        return _markdownContentEditor;
    }

    /**
     * 处理行内式链接
     * @param _markdownContentEditor MarkdownContentEditor对象
     * @return 返回一个已经处理行内式链接的MarkdownContentEditor对象
     */
    private MarkdownContentEditor doInlineLinks(MarkdownContentEditor _markdownContentEditor){
        /*
         * 例如：
         *    [从林的Github](https://github.com/CongLinDev "这是一个可选的标题")
         */
        Pattern inlineLink = Pattern.compile("(" + // 全部匹配 = $1
                "\\[(.*?)\\]" + // 链接文本 是 $2
                "\\(" +
                "[ \\t]*" +
                "<?(.*?)>?" + // href 是 $3
                "[ \\t]*" +
                "(" +
                "(['\"])" + // 引用字符 是 $5
                "(.*?)" + // 标题 是 $6
                "\\5" +
                ")?" +
                "\\)" +
                ")", Pattern.DOTALL);
        _markdownContentEditor.replaceAll(inlineLink, new Replacement(){
           @Override
           public String replacementString(Matcher matcher) {
               String linkContent = matcher.group(2);
               String url = matcher.group(3);
               String title = matcher.group(6);

               StringBuffer replacementText = new StringBuffer();
               replacementText.append("<a href=\"").append(url).append("\"");
               if(title != null){
                   title.replaceAll("\"", "&quot;");
                   replacementText.append(" title=\"");
                   replacementText.append(title);
                   replacementText.append("\"");
               }
               replacementText.append(">").append(linkContent).append("</a>");
               return replacementText.toString();
           }
        });
        return _markdownContentEditor;
    }

    /**
     * 处理图片
     * @param _markdownContentEditor MarkdownContentEditor对象
     * @return 返回一个已经处理图片的MarkdownContentEditor对象
     */
    private MarkdownContentEditor doImages(MarkdownContentEditor _markdownContentEditor){
        //内嵌图片
        _markdownContentEditor.replaceAll("!\\[(.*)\\]\\((.*) \"(.*)\"\\)", "<img src=\"$2\" alt=\"$1\" title=\"$3\" />");
        _markdownContentEditor.replaceAll("!\\[(.*)\\]\\((.*)\\)", "<img src=\"$2\" alt=\"$1\" />");

        // 参考式图片
        Pattern imageLink = Pattern.compile("(" +
                "[!]\\[(.*?)\\]" + // alt text 是 $2
                "[ ]?(?:\\n[ ]*)?" +
                "\\[(.*?)\\]" + // ID 是 $3
                ")");
        _markdownContentEditor.replaceAll(imageLink, new Replacement() {
            @Override
            public String replacementString(Matcher matcher) {
                String replacementText;
                String wholeMatch = matcher.group(1);
                String altText = matcher.group(2);
                String id = matcher.group(3).toLowerCase();
                if ("".equals(id)) {
                    id = altText.toLowerCase();
                }

                LinkDefinition linkDefinition = linkDefinitions.get(id);
                if (linkDefinition != null) {
                    String url = linkDefinition.getUrl();
                    String title = linkDefinition.getTitle();
                    String titleTag = "";
                    if (title != null && !title.equals("")) {
                        titleTag = " alt=\"" + altText + "\" title=\"" + title + "\"";
                    }
                    replacementText = "<img src=\"" + url + "\"" + titleTag + "/>";
                } else {
                    replacementText = wholeMatch;
                }
                return replacementText;
            }
        });
        return _markdownContentEditor;
    }

    /**
     * 处理列表
     * @param _markdownContentEditor MarkdownContentEditor对象
     * @return 返回一个已经处理列表的MarkdownContentEditor对象
     */
    private MarkdownContentEditor doLists(MarkdownContentEditor _markdownContentEditor){

        String wholeList =  "(" +
                "(" +
                "[ ]{0," + MarkdownContentEditor.LESS_TAB_WIDTH + "}" +
                "((?:[-+*]|\\d+[.]))" + // $3 是第一个列表项标记
                "[ ]+" +
                ")" +
                "(?s:.+?)" +
                "(" +
                "\\z" +                 //到此结束即匹配成功
                "|" +
                "\\n{2,}" +
                "(?=\\S)" +             // 如果没有结束就从下一个段落开始
                "(?![ ]*" +
                "(?:[-+*]|\\d+[.])" +   // 下一个列表标记
                "[ ]+" +
                ")))";
        Replacement tempReplacement = new Replacement() {
            @Override
            public String replacementString(Matcher matcher) {
                String matchedListContent = matcher.group(1);
                String listStart = matcher.group(3);        //列表标记，区分有序和无序
                String listItems = processListItems(matchedListContent); //list的最终结果
                listItems = listItems.replaceAll("\\s+$", "");//去除后续多余空格

                String listResult;
                //如果listStart匹配 *或+或-    即为无序列表
                if(listStart.matches("[*+-]")){
                    listResult = "<ul>" + listItems + "</ul>\n";
                }else {                     //否则为有序列表
                    listResult = "<ol>" + listItems + "</ol>\n";
                }
                return listResult;
            }
        };
        if(listLevel > 0){
            Pattern matchStartOfLine = Pattern.compile("^" + wholeList, Pattern.MULTILINE);
            _markdownContentEditor.replaceAll(matchStartOfLine, tempReplacement);
        }else {
            Pattern matchStartOfLine = Pattern.compile("(?:(?<=\\n\\n)|\\A\\n?)" + wholeList, Pattern.MULTILINE);
            _markdownContentEditor.replaceAll(matchStartOfLine, tempReplacement);
        }

        return _markdownContentEditor;
    }

    /**
     * 处理列表内元素
     * @param list String对象
     * @return 返回一个已经处理列表元素的String对象
     */
    private String processListItems(String list){
        listLevel++;

        list = list.replaceAll("\\n{2,}\\z", "\n");//删除多余空行
        Pattern pattern = Pattern.compile("(\\n)?" +
                        "^([ \\t]*)([-+*]|\\d+[.])[ ]+" +
                        "((?s:.+?)(\\n{1,2}))" +
                        "(?=\\n*(\\z|\\2([-+\\*]|\\d+[.])[ \\t]+))",
                Pattern.MULTILINE);
        MarkdownContentEditor tempList = new MarkdownContentEditor(list);

        tempList = tempList.replaceAll(pattern, new Replacement(){
            @Override
            public String replacementString(Matcher matcher){
                MarkdownContentEditor tempItem = new MarkdownContentEditor(matcher.group(4));
                String leadingLine = matcher.group(1);

                //如果不是空串或者是有下一个段落，处理段落内元素
                if(!(leadingLine == null|| leadingLine.equals("")) || tempItem.toString().indexOf("\n\n") != -1){
                    tempItem = runBlockItem(tempItem);
                }else{
                    //递归处理
                    tempItem = doLists(tempItem);
                    tempItem = runSpanItem(tempItem);       //处理跨区域元素
                }
                return "<li>" + tempItem.toString() + "</li>\n";
            }
        });

        listLevel--;
        return tempList.toString();
    }
}
