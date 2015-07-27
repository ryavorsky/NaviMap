package com.navimap.utils;

import com.google.android.gms.maps.model.LatLng;

public class NaviSupport {
    private static final double k1 = 111111.1; //
    private static final double k2 = 62647.6;  //
    // default navi address template (+%country%%city%) %navi_4_digit% %navi_4_digit%
    // (+7495) 1234 1234
/*	@Deprecated
    public static String GetNavi8(double lat, double lng){
		double d1 = (lat-c1)*k1; // ��������� �������� �� ������ �� ������ ����������
		double d2 = (lng-c2)*k2; // � ��������� � �����

		int navi1 = (int)Math.round(d1/10) + 5000;
		int navi2 = (int)Math.round(d2/10) + 5000;

		return navi1+" "+ navi2;
	}

	@Deprecated
	public static LatLng GetLatLngNavi8(int Navi){
		int n1 = (int)Math.floor(Navi/10000);
		int n2 = Navi % 10000;
		
		double d1 = (double)(n1 - 5000)*10/k1; // ��������� �������� � ��������
		double d2 = (double)(n2 - 5000)*10/k2; // �� ������ ������
		
		return new LatLng(c1 + d1, c2 + d2);
	}*/

    @Deprecated
    public static LatLng GetLatLngNavi6(String city, int Navi) {
        String str = NaviMapUtils.getAddr(city, Integer.toString(Navi));
        if (str == null) return null;
        return NaviMapUtils.getLatLng(str, "");
    }

    @Deprecated
    public static String getLink(String code) {
        return "http://navic.me/" + code.replace("(", "").replace("+", "0").replace(") ", ".").replace(" ", "");
    }


    public static String getNavi8Code(LatLng latLng) {
        MapUtils.City city = MapUtils.City.getNearestCity(latLng);

        Double[] distances = MapUtils.getDistancesToAxis(latLng, city.getLatLng());

        int navi1 = (int) Math.round(distances[0] / 10) + 5000;
        int navi2 = (int) Math.round(distances[1] / 10) + 5000;

        if (navi1 < 0 || navi1 > 9999 || navi2 < 0 || navi2 > 9999) {
            distances = MapUtils.getZeroAxisNaviAddress(latLng);
            int[]dist = new int[2];
            dist[0] = (int) (distances[0]/10);
            dist[1] = (int) (distances[1]/10);
            String code = String.valueOf(dist[0]).substring(0,3)+String.valueOf(dist[1]).substring(0,3);
            String index = String.format("%04d",dist[0] % 10000) + " " + String.format("%04d", dist[1] % 10000);
            return "(" + code + ") " + index;
        } else {
            return "(" + city.getNaviCode().replaceFirst("0", "+") + ") " +  String.format("%04d",navi1) + " " +  String.format("%04d",navi2);
        }
    }

    public static LatLng GetLatLngNavi8(String code) {
        String cityNaviCode = code.substring(0, code.indexOf(" ")).replace("(", "").replace(")", "").replace("+", "0");

        MapUtils.City city = MapUtils.City.getCity(cityNaviCode);
        String naviCode = code.substring(code.indexOf(" ") + 1);
        String[] naviCodes = naviCode.split(" ");
        int n1 = Integer.parseInt(naviCodes[0]);
        int n2 = Integer.parseInt(naviCodes[1]);

        double d1 = (double) (n1 - 5000) * 10 / k1; // ��������� �������� � ��������
        double d2 = (double) (n2 - 5000) * 10 / k2; // �� ������ ������

        return new LatLng(city.getLat() + d1, city.getLng() + d2);
    }
}
