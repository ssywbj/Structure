package com.suheng.wallpaper.roamimg.city;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.suheng.wallpaper.roamimg.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class SsWorldTimeData {
    private static List<SsCity> mSearchList = null;

    public static void clearSearchList() {
        if (mSearchList == null) {
            return;
        }
        mSearchList.clear();
        mSearchList = null;
    }

    /**
     * parser the xml to get the worldtime message
     */
    public static synchronized List<SsCity> getWorldTimeZones(Context context) {
        if (null != mSearchList) {
            return mSearchList;
        }
        List<SsCity> cityList = new ArrayList<>();
        try {
            XmlResourceParser xrp = context.getResources().getXml(R.xml.timezones);
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String tagName = xrp.getName();
                    if (tagName.equals(SsWorldTimeConstants.XMLTAG_TIMEZONE)) {
                        String id = xrp.getAttributeValue(0);
                        String pinyin = xrp.getAttributeValue(1);
                        String displayName = xrp.nextText();
                        addListItem(cityList, id, displayName, pinyin);
                    }
                }
                xrp.next();
            }

            xrp.close();
        } catch (XmlPullParserException | IOException xppe) {
            xppe.printStackTrace();
        }

        mSearchList = cityList;
        return cityList;
    }

    private static void addListItem(List<SsCity> ssCities, String id, String displayName, String pinyin) {
        final long date = Calendar.getInstance().getTimeInMillis();
        SsCity city = new SsCity();
        city.setId(id);
        city.setName(displayName);
        city.setPinyin(pinyin);
        String tz_id = SsViewUtils.formatTimeZoneId(id);

        String gmt = SsViewUtils.getCityGmt(tz_id);
        final TimeZone tz = TimeZone.getTimeZone(tz_id);
        final int offset = tz.getOffset(date);
        city.setGmt(gmt);
        city.setOffset(offset);
        ssCities.add(city);
    }

}
