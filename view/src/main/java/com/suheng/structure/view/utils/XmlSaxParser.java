package com.suheng.structure.view.utils;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.suheng.structure.view.R;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class XmlSaxParser extends DefaultHandler {
    private static final String TAG = XmlSaxParser.class.getSimpleName();

    int currentState = 0;
    final int ITEM = 0x0005;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        Log.d(TAG, "-----startDocument-----");
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        Log.d(TAG, "startElement, uri: " + uri + ", localName: " + localName + ", qName: " + qName);
        if ("item".equals(localName)) {
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getLocalName(i).equals("id")) {
                    Log.i(TAG, "startElement, id: " + attributes.getValue(i));
                    //chann.setId(attributes.getValue(i));
                } else if (attributes.getLocalName(i).equals("url")) {
                    Log.i(TAG, "startElement, uri: " + attributes.getValue(i));
                    //chann.setUrl(attributes.getValue(i));
                }
            }

            currentState = ITEM;
            return;
        }

        currentState = 0;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        Log.d(TAG, "characters, ch: " + new String(ch) + ", start: " + start + ", length: " + length);
        String theString = String.valueOf(ch, start, length);
        if (currentState != 0) {
            //chann.setName(theString);
            Log.i(TAG, "characters, theString: " + theString);
            currentState = 0;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        Log.d(TAG, "endElement, uri: " + uri + ", localName: " + localName + ", qName: " + qName);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        Log.d(TAG, "-----endDocument-----");
    }

    public void getChannelList(Context context, int rawId) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        XmlSaxParser xmlSaxParser = new XmlSaxParser();
        xmlReader.setContentHandler(xmlSaxParser);
        InputStream inputStream = context.getResources().openRawResource(rawId);
        InputSource inputSource = new InputSource(inputStream);
        xmlReader.parse(inputSource);
    }

    public void getChannelList(Context context){
        XmlResourceParser parser = context.getResources().getXml(R.xml.xml_temp);
        try {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();// 获取标签的名字
                    if (tagName.equals("item")) {
                        String id = parser.getAttributeValue(null, "id"); //通过属性名来获取属性值
                        String url = parser.getAttributeValue(1); //通过属性索引来获取属性值
                        String name = parser.nextText(); //通过属性索引来获取属性值
                        Log.i(TAG, "id: " + id + ", url: " + url + ", name: " + name);
                    }
                }

                parser.next();// 获取解析下一个事件
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
