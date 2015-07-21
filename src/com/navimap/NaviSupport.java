package com.navimap;

import com.google.android.gms.maps.model.LatLng;

public class NaviSupport {
	
	private static final double c1 = 55.7522200; // ������
	private static final double c2 = 37.6155600; // �������
	private static final double k1 = 111111.1; // ����� ������ ������� �� ������ � ������
	private static final double k2 = 62647.6;  // ����� ������ ������� �� ������� � ������ ��� ������
	
	public static String GetNavi8(double lat, double lng){
		double d1 = (lat-c1)*k1; // ��������� �������� �� ������ �� ������ ����������
		double d2 = (lng-c2)*k2; // � ��������� � �����

		int navi1 = (int)Math.round(d1/10) + 5000;
		int navi2 = (int)Math.round(d2/10) + 5000;
		
		return navi1+" "+ navi2;
	}

	public static LatLng GetLatLngNavi8(int Navi){
		int n1 = (int)Math.floor(Navi/10000);
		int n2 = Navi % 10000;
		
		double d1 = (double)(n1 - 5000)*10/k1; // ��������� �������� � ��������
		double d2 = (double)(n2 - 5000)*10/k2; // �� ������ ������
		
		return new LatLng(c1 + d1, c2 + d2);
	}
	
	public static LatLng GetLatLngNavi6(String city, int Navi){
		String str = NaviMapServices.getAddr(city, Integer.toString(Navi));
		if(str==null) return null;
		return NaviMapServices.getLatLng(str,"");
	}
}
