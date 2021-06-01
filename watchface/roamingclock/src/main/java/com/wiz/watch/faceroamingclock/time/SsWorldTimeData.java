package com.wiz.watch.faceroamingclock.time;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.wiz.watch.faceroamingclock.R;

import java.util.ArrayList;
import java.util.List;

public class SsWorldTimeData {
    private static List<SsCity> mSearchList = new ArrayList<>();

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
        clearSearchList();
        List<SsCity> cityList = new ArrayList<>();
        try {
            XmlResourceParser xrp = context.getResources().getXml(R.xml.timezones);
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String tagName = xrp.getName();
                    if (tagName.equals(SsWorldTimeConstants.XMLTAG_TIMEZONE)) {
                        String id = xrp.getAttributeValue(0);
                        String displayName = xrp.nextText();
                        addListItem(cityList, id, displayName);
                    }
                }
                xrp.next();
            }

            xrp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSearchList = cityList;
        return cityList;
    }

    private static void addListItem(List<SsCity> ssCities, String id, String displayName) {
        SsCity city = new SsCity();
        city.setId(id);
        city.setName(displayName);
        String tz_id = SsViewUtils.formatTimeZoneId(id);

        String gmt = SsViewUtils.getCityGmt(tz_id);
        city.setGmt(gmt);
        ssCities.add(city);
    }

}
