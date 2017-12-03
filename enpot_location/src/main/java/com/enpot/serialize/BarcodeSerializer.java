package com.enpot.serialize;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.enpot.reflect.FieldClass;
import com.enpot.web.Converter;

import android.util.Log;
import android.util.Xml;

public class BarcodeSerializer
{


    public static HashMap<String, HashMap<String, Field>> FIELD_MAP = new HashMap();

    public static HashMap<String, Field> GetFieldMap(Class<?> c)
    {
        if (Converter.FIELD_MAP.containsKey(c.getSimpleName()))
        {
            return Converter.FIELD_MAP.get(c.getSimpleName());
        }
        else
        {
            Field[] fs = c.getDeclaredFields();
            HashMap<String, Field> fm = new HashMap();
            for (int i = 0; i < fs.length; i++)
            {
                fm.put(fs[i].getName(), fs[i]);
            }

            Converter.FIELD_MAP.put(c.getSimpleName(), fm);

            return fm;
        }
    }

    public static String Serialize(Object data, XmlPullParser parser) throws XmlPullParserException, IOException, IllegalAccessException, IllegalArgumentException
    {
        String content = "";

        HashMap<String, Field> fm = BarcodeSerializer.GetFieldMap(data.getClass());

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                {
                    if (parser.getAttributeCount() <= 0)
                    {
                        break;
                    }

                    String field_name = parser.getAttributeValue(0);
                    if (!fm.containsKey(field_name))
                    {
                        break;
                    }

                    //讀Text
                    parser.next();
                    Field f = fm.get(field_name);
                    content += MessageFormat.format(parser.getText(), f.get(data));

                    break;
                }
                case XmlPullParser.END_TAG:
                {
                    break;
                }
            }
            eventType = parser.next();
        }

        return content;
    }

    public static String Serialize(Object data, String xml) throws XmlPullParserException, IOException, IllegalAccessException, IllegalArgumentException
    {
        ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(stream, "UTF-8");

        return BarcodeSerializer.Serialize(data, parser);
    }

    public static <T> T Deserialize(Class<T> c, String barcode, XmlPullParser parser) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, XmlPullParserException, IOException, NoSuchFieldException, ClassNotFoundException
    {
        Converter.FIELD_MAP.clear();
        HashMap<String, Field> fm = Converter.GetFieldMap(c);

        T t = c.newInstance();
        Field f = null;

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {

            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                {
                    if (parser.getAttributeCount() <= 0)
                    {
                        f = null;
                        break;
                    }

                    String field_name = parser.getAttributeValue(0);
                    if (!fm.containsKey(field_name))
                    {
                        f = null;
                        break;
                    }

                    f = fm.get(field_name);
                    break;
                }
                case XmlPullParser.TEXT:
                {
                    if (f == null)
                    {
                        break;
                    }

                    String regEx = parser.getText();
                    Pattern pattern = Pattern.compile(regEx);
                    Matcher matcher = pattern.matcher(barcode);
                    if (matcher == null)
                    {
                        break;
                    }

                    if (matcher.find())
                    {
                        String text = matcher.group(0);
                        regEx = "(?!([A-Za-z]*:))[^|]*";
                        pattern = Pattern.compile(regEx);
                        matcher = pattern.matcher(text);

                        if (matcher.find())
                        {
                            String value = matcher.group(0);
//                    		f.set(t, value);

                            Constructor con = f.getType().getDeclaredConstructor(String.class);
                            f.set(t, con.newInstance(value));
                        }
                    }
                    break;
                }
                case XmlPullParser.END_TAG:
                {
                    f = null;
                    break;
                }
            }
            eventType = parser.next();
        }


        return t;
    }


    public static <T> T Deserialize(Class<T> c, String barcode, String xml) throws XmlPullParserException, IOException, IllegalAccessException, IllegalArgumentException, NoSuchMethodException, InstantiationException, InvocationTargetException, NoSuchFieldException, ClassNotFoundException
    {
        ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(stream, "UTF-8");

        return BarcodeSerializer.Deserialize(c, barcode, parser);
    }

//
//    private static String SerializeNode(Object data, XmlNode node)
//    {
//        string content = string.Empty;
//
//        object value = BarcodeSerializer.SerializePropertyValue(node.Attributes["PropertyName"].Value, data);
//
//        if (value is ICollection)
//        {
//            foreach (object item in (ICollection)value)
//            {
//                string itemContent = string.Empty;
//                foreach (XmlNode temp in node.ChildNodes)
//                {
//                    itemContent = itemContent + BarcodeSerializer.SerializeNode(item, temp);
//                }
//
//                content = content + string.Format(node.Attributes["ItemsParrent"].Value, itemContent);
//            }
//        }
//        else
//        {
//            content = string.Format(node.InnerText, value == null ? string.Empty : value.ToString());
//        }
//        return content;
//    }


}
