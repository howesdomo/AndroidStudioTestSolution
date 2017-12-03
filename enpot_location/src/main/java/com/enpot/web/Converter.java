package com.enpot.web;

import android.util.Log;
import android.util.Xml;

import com.enpot.reflect.FieldClass;
import com.enpot.utils.EnpotLog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



/**
 * Converter
 * <p>
 * <p>XML2JavaBean & JavaBean2XML Converter</p>
 *
 * @LastestAuthor Howe
 * @version 1.1
 */
public class Converter
{

    public static int Count = 0;

    public static HashMap<String, HashMap<String, Field>> FIELD_MAP = new HashMap();

    public static HashMap<String, Field> GetFieldMap(Class<?> c)
    {
        if (Converter.FIELD_MAP.containsKey(c.getSimpleName()))
        {
            return Converter.FIELD_MAP.get(c.getSimpleName());
        }
        else
        {
//			Field[] fs = c.getDeclaredFields();  // 本类修改属性权限
            Field[] fs = c.getFields();          // 继承的父类可以修改属性

            HashMap<String, Field> fm = new HashMap();
            for (int i = 0; i < fs.length; i++)
            {
                Field f = fs[i];
                String key = f.getName();
                fm.put(key, f);
            }

            Converter.FIELD_MAP.put(c.getSimpleName(), fm);

            return fm;
        }
    }


    /**
     * List<T> 需要进入到 XmlPullParser.TEXT 中处理的类型
     */
    public static List<String> SpecialTypeList = new ArrayList<String>();

    /**
     * 无法使用Construction构建的基础类型
     */
    public static List<String> UnableConstructionList = new ArrayList<String>();

    static
    {
        if (SpecialTypeList != null && SpecialTypeList.size() == 0)
        {
            SpecialTypeList.add("String");
            SpecialTypeList.add("DateTime");
        }

        if (UnableConstructionList != null && UnableConstructionList.size() == 0)
        {
            UnableConstructionList.add("Integer");
            UnableConstructionList.add("Long");
            UnableConstructionList.add("Double");
            UnableConstructionList.add("BigDecimal");
            UnableConstructionList.add("Boolean");
        }
    }

    private static Object XMLToObject(Class<?> c, HashMap<String, Field> fm, XmlPullParser parser, String startName)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, XmlPullParserException, IOException, NoSuchFieldException,
            ClassNotFoundException
    {
        Object t = c.newInstance();
        String nodeName = null;

        boolean isInsertParserText = false; // 插入insertParserText 到 t
        String insertItemClassName = "";

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            //region switch eventType
            switch (eventType)
            {
                case XmlPullParser.START_TAG: // eventType == 2
                {
                    nodeName = parser.getName();

                    if (parser.getAttributeCount() > 0 && parser.getAttributeValue(0).equals("true"))
                    {
                        break;
                    }

                    if (nodeName.equals(c.getSimpleName()))
                    {
                        if (SpecialTypeList.contains(nodeName))
                        {
                            isInsertParserText = true;
                        }
                        break;
                    }
                    else
                    {
                        // Field f = c.getDeclaredField(nodeName);
                        Field f = fm.get(nodeName);
                        if (f != null)
                        {
                            parser.next();
                            FieldClass fc = f.getAnnotation(FieldClass.class);
                            if (fc != null)
                            {
                                Class itemClass = Class.forName(fc.ClassName());
                                HashMap<String, Field> tempFM = Converter.GetFieldMap(itemClass);

                                if (fc.IsList())
                                {
                                    if (UnableConstructionList.contains(itemClass.getSimpleName())) // Add By Howe 无法使用Construction构建的基础类型List
                                    {
                                        insertItemClassName = itemClass.getName();
                                        ArrayList items = new ArrayList();
                                        Converter.XMLToUnableConstructionList(items, insertItemClassName, parser, nodeName);
                                        f.set(t, items);
                                    }
                                    else // List<自定义类型>
                                    {
                                        ArrayList items = Converter.XMLToObjects(ArrayList.class, itemClass, tempFM, parser, nodeName);
                                        f.set(t, items);
                                    }
                                }
                                else // 自定义类型
                                {
                                    Object item = Converter.XMLToObject(itemClass, tempFM, parser, nodeName);
                                    f.set(t, item);
                                }
                            }
                            else
                            {
                                if (parser.getText() != null)
                                {
                                    Constructor con = f.getType().getDeclaredConstructor(String.class);
                                    f.set(t, con.newInstance(parser.getText()));
                                }
                            }
                        }
                    }
                    break;
                }

                case XmlPullParser.END_TAG: // eventType == 3
                {
                    if (parser.getName().equals(startName))
                    {
                        Log.i("END", t.getClass().getName() + " ------ " + startName);
                        return t;
                    }
                    break;
                }

                // Add By Howe
                case XmlPullParser.TEXT:
                {
                    if (c.getCanonicalName().endsWith("ArrayList"))
                    {
                        if (parser != null && parser.getText() != null && parser.getText().equals("") == false)
                        {
                            EnpotLog.i(parser.getText());

                            ArrayList arrayList = (ArrayList) t;
                            arrayList.add(parser.getText());
                        }
                    }

                    if (isInsertParserText)
                    {
                        if (parser != null && parser.getText() != null && parser.getText().equals("") == false)
                        {
                            Constructor con = c.getDeclaredConstructor(String.class);
                            t = con.newInstance(parser.getText());
                        }
                    }
                }
                break;
            }
            //endregion switch eventType

            eventType = parser.next();
        }
        return null;
    }

    /**
     * Add By Howe
     * @param list
     * @param insertItemClassName
     * @param parser
     * @param startNodeName
     * @throws XmlPullParserException
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static void XMLToUnableConstructionList(ArrayList list, String insertItemClassName, XmlPullParser parser, String startNodeName)
            throws XmlPullParserException, XmlPullParserException, IOException
    {
        boolean continueWhile = true;
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT && continueWhile == true)
        {
            switch (eventType)
            {
                case XmlPullParser.START_TAG: // eventType == 2
                {
                    break;
                }
                case XmlPullParser.TEXT:
                {
                    switch (insertItemClassName.toUpperCase())
                    {
                        case "JAVA.LANG.INTEGER":
                        {
                            Integer tmp = Integer.parseInt(parser.getText());
                            list.add(tmp);
                            break;
                        }
                        case "JAVA.LANG.LONG":
                        {
                            Long tmp = Long.parseLong(parser.getText());
                            list.add(tmp);
                            break;
                        }
                        case "JAVA.LANG.DOUBLE":
                        {
                            Double tmp = Double.parseDouble(parser.getText());
                            list.add(tmp);
                            break;
                        }
                        case "JAVA.MATH.BIGDECIMAL":
                        {
                            BigDecimal tmp = new BigDecimal(parser.getText());
                            list.add(tmp);
                            break;
                        }
                        case "JAVA.LANG.BOOLEAN":
                        {
                            Boolean tmp = Boolean.parseBoolean(parser.getText());
                            list.add(tmp);
                            break;
                        }
                        default:
                            break;
                    }
                    break;
                }
                case XmlPullParser.END_TAG:
                {
                    if (parser.getName().equals(startNodeName))
                    {
                        continueWhile = false;
                    }
                }
                default:
                    break;
            }

            if (continueWhile == true)
            {
                parser.next();
                eventType = parser.getEventType();
            }
        }
    }

    public static String XMLToString(XmlPullParser parser)
            throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, XmlPullParserException,
            IOException, NoSuchFieldException, ClassNotFoundException
    {
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                {
                    if (parser.getName().endsWith(Fault.class.getSimpleName()))
                    {

                    }
                    else if (parser.getName().endsWith("Result"))
                    {
                        parser.next();
                        return parser.getText().toString();
                    }
                    break;
                }
            }
            eventType = parser.next();
        }

        return null;
    }

    private static ArrayList XMLToObjects(Class<?> listClass, Class<?> itemClass, HashMap<String, Field> fm,
                                          XmlPullParser parser, String startName)
            throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, XmlPullParserException,
            IOException, NoSuchFieldException, ClassNotFoundException
    {
        ArrayList list = (ArrayList) listClass.newInstance();
        String nodeName = null;

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                {
                    nodeName = parser.getName();
                    if (parser.getAttributeCount() > 0 && parser.getAttributeValue(0).equals("true"))
                    {
                        break;
                    }

                    if (nodeName.equals(itemClass.getSimpleName()))
                    {
                        Object obj = Converter.XMLToObject(itemClass, fm, parser, nodeName);
                        list.add(obj);
                    }
                    break;
                }
                case XmlPullParser.END_TAG:
                {
                    if (parser.getName().equals(startName))
                    {
                        return list;
                    }
                    break;
                }
            }
            eventType = parser.next();
        }
        return list;
    }

    /**
     * XML To List<Object>
     */
    public static <T> ArrayList<T> XMLToObjects(Class<T> c, String xml) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            XmlPullParserException, IOException, NoSuchFieldException, ClassNotFoundException
    {
        Converter.FIELD_MAP.clear();
        HashMap<String, Field> fm = Converter.GetFieldMap(c);

        ArrayList list = null;
        String nodeName = null;

        ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(stream, "UTF-8");

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {

            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                {
                    Log.i("a", "d");
                    nodeName = parser.getName();

                    if (nodeName.equals(c.getSimpleName()))
                    {
                        list = (ArrayList) Converter.XMLToObjects(ArrayList.class, c, fm, parser, nodeName);
                    }

                    break;
                }
            }
            eventType = parser.next();
        }

        return list;
    }

    /**
     * XML To Object
     */
    public static <T> T XMLToObject(Class<T> c, String xml) throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, XmlPullParserException,
            IOException, NoSuchFieldException, ClassNotFoundException
    {
        ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
        return Converter.XMLToObject(c, stream);
    }

    public static <T> T XMLToObject(Class<T> c, InputStream stream) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            XmlPullParserException, IOException, NoSuchFieldException, ClassNotFoundException
    {
        Converter.FIELD_MAP.clear();
        HashMap<String, Field> fm = Converter.GetFieldMap(c);

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(stream, "UTF-8");

        // 只返回最簡單的字符串
        if (c.equals(String.class))
        {
            return (T) Converter.XMLToString(parser);
        }

        T t = null;
        String nodeName = null;
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                {
                    nodeName = parser.getName();
                    return (T) Converter.XMLToObject(c, fm, parser, nodeName);
                }
            }
            eventType = parser.next();
        }

        return null;
    }

    //region Object2XML

    public static String ObjectToXML(String nameSpce, String methodName, Map<String, Object> params)
            throws IllegalArgumentException, IllegalStateException, IOException, ClassNotFoundException,
            IllegalAccessException
    {
        Converter.FIELD_MAP.clear();

        String xml = "";
        String soapenv = "http://schemas.xmlsoap.org/soap/envelope/";

        XmlSerializer serializer = Xml.newSerializer(); // 由android.util.Xml创建一个XmlSerializer实例
        StringWriter writer = new StringWriter();
        serializer.setOutput(writer); // 设置输出方向为writer
        serializer.startDocument("UTF-8", true);
        serializer.setPrefix("soapenv", soapenv);
        serializer.setPrefix("e", nameSpce);
        serializer.startTag(soapenv, "Envelope");

        serializer.startTag(soapenv, "Header");
        serializer.endTag(soapenv, "Header");
        serializer.startTag(soapenv, "Body");

        serializer.startTag(nameSpce, methodName);

        if (params != null)
        {
            Iterator<Map.Entry<String, Object>> item = params.entrySet().iterator();
            while (item.hasNext())
            {
                Map.Entry<String, Object> entry = item.next();
                String key = entry.getKey();
                Object obj = entry.getValue();

                Class c = obj.getClass();
                Converter.ObjectToXML(serializer, nameSpce, key, obj);
            }
        }

        serializer.endTag(nameSpce, methodName);

        serializer.endTag(soapenv, "Body");
        serializer.endTag(soapenv, "Envelope");
        serializer.endDocument();
        xml = writer.toString();

        Log.i("DONE", xml);

        return xml;
    }

    private static void ObjectToXMLOfNull(XmlSerializer serializer, String nameSpce, String key)
            throws IllegalArgumentException, IllegalStateException, IOException, ClassNotFoundException,
            IllegalAccessException
    {
        serializer.startTag(nameSpce, key);
        serializer.text("null");
        serializer.endTag(nameSpce, key);
    }

    private static void ObjectToXML(XmlSerializer serializer, String nameSpce, String key, Object obj)
            throws IllegalArgumentException, IllegalStateException, IOException, ClassNotFoundException,
            IllegalAccessException
    {
        serializer.startTag(nameSpce, key);
        if (obj != null)
        {
            Class c = obj.getClass();
            if (c.getCanonicalName().endsWith("[]") || c.getCanonicalName().endsWith("List"))
            {
                // Log.i("ARRAY","aaaaaa");
                Converter.ObjectsToXML(serializer, nameSpce, key, obj);
            }
            // 非自定義類型,不需要反射o
            else if (c.getCanonicalName().startsWith("java"))
            {
                if (c.getCanonicalName().startsWith("java.util.Date"))
                {
                    serializer.text(((java.util.Date) obj).toString());
                }
                else
                {
                    serializer.text(obj.toString()); // 非自定義類型,不需要反射o
                }
            }
            else if (c.getCanonicalName().equals("com.enpot.utils.DateTime"))
            {
                serializer.text(((com.enpot.utils.DateTime) obj).toString());
            }
            else
            {
                HashMap<String, Field> fm = Converter.GetFieldMap(c);
                Iterator<Map.Entry<String, Field>> item = fm.entrySet().iterator();
                while (item.hasNext())
                {
                    Map.Entry<String, Field> entry = item.next();
                    String name = entry.getKey();
                    if (name.equals("$change") || name.equals("serialVersionUID") || name.equals("IsComplete"))
                    {
                        continue;
                    }
                    Field f = (Field) entry.getValue();
                    FieldClass fc = f.getAnnotation(FieldClass.class);
                    if (fc != null && fc.IsList())
                    {
                        serializer.startTag(nameSpce, name);
                        Converter.ObjectsToXML(serializer, nameSpce, name, f.get(obj));
                        serializer.endTag(nameSpce, name);
                    }
                    else
                    {
                        Converter.ObjectToXML(serializer, nameSpce, name, f.get(obj));
                    }
                }
            }
        }
        serializer.endTag(nameSpce, key);
    }

    private static void ObjectsToXML(XmlSerializer serializer, String nameSpce, String key, Object list)
            throws IllegalArgumentException, IllegalStateException, IOException, ClassNotFoundException,
            IllegalAccessException
    {
        // serializer.startTag(nameSpce, key);
        if (list != null)
        {
            Class c = list.getClass();
            // Log.i("LIST c", c.getCanonicalName() + " " + c.isArray());
            // Log.i("LIST cc", cc.getCanonicalName() + " " + cc.isArray());
            if (c.getCanonicalName().endsWith("[]"))
            {
                Class cc = c.getComponentType();
                Object[] objs = (Object[]) list;
                for (int i = 0; i < objs.length; i++)
                {
                    Converter.ObjectToXML(serializer, nameSpce, cc.getSimpleName(), Array.get(objs, i));
                }
            }
            else if (c.getCanonicalName().endsWith("List"))
            {
                List objs = (List) list; // Edit By Howe 应该使用List接口, 不要用具体的实现 如 ArrayList , LinkedList 等等
                String classSimpleName = null;

                //region 获取那非null的对象然后获取集合的泛型

                for (int i = 0; i < objs.size(); i++)
                {
                    Object obj = objs.get(i);
                    if (obj != null)
                    {
                        classSimpleName = obj.getClass().getSimpleName();
                    }
                    if (classSimpleName != null && classSimpleName.equals("") == false)
                    {
                        break;
                    }
                }

                //endregion

                for (int i = 0; i < objs.size(); i++)
                {
                    Object obj = objs.get(i);
                    Converter.ObjectToXML(serializer, nameSpce, classSimpleName, obj);

                    // 旧版代码
//                    if (obj == null) // Edit By Howe 对于List中某项Item 是 Null的处理
//                    {
//                        //region (注释相关代码)暂时无法直接在此获取泛型
//
////                        Field[] fs = c.getDeclaredFields();
////                        for (Field f : fs) {
////                            Class fieldClazz = f.getType(); // 得到field的class及类型全路径
////
////                            if (fieldClazz.isPrimitive()) continue;  //【1】 //判断是否为基本类型
////
////                            if (fieldClazz.getName().startsWith("java.lang"))
////                                continue; //getName()返回field的类型全路径；
////
////                            if (fieldClazz.isAssignableFrom(List.class)) //【2】
////                            {
////                                Type fc = f.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型
////                                if (fc == null) continue;
////                                if (fc instanceof ParameterizedType) // 【3】如果是泛型参数的类型
////                                {
////                                    ParameterizedType pt = (ParameterizedType) fc;
////                                    Class genericClazz = (Class) pt.getActualTypeArguments()[0]; //【4】 得到泛型里的class类型对象。
////                                    String name = f.getName();
////                                }
////                            }
////                        }
//
//                        //endregion
//
//                        EnpotLog.i("null");
//                        Converter.ObjectToXMLOfNull(serializer, nameSpce, classSimpleName); // TODO Howe 如何直接再此if中获取List的泛型
//                    }
//                    else {
//                        Converter.ObjectToXML(serializer, nameSpce, obj.getClass().getSimpleName(), obj);
//                    }
                }
            }
        }
        // serializer.endTag(nameSpce, key);
    }

    public static <T> T TestReflection(Class<T> c) throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {

        T t = c.newInstance();

        Field[] f = c.getFields();
        for (int i = 0; i < f.length; i++)
        {
            f[i].set(t, "aaa");
        }

        return t;
    }

    public static <T> void TestReflection(T t) throws IllegalAccessException, IllegalArgumentException
    {

        Log.i("TestReflection", "ccccc");

        Field[] f = t.getClass().getFields();
        for (int i = 0; i < f.length; i++)
        {
            Log.i("REF", f[i].getName() + "   " + String.valueOf(f[i].get(t)));
        }
        Log.i("TestReflection", String.valueOf(f.length));
    }

    //endregion Object2XML

}
