package com.sand;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class gziptest {
	static String hstr = "485454502F312E3120323030204F4B0D0A446174653A205765642C2031372053657020323031342031323A34313A323620474D540D0A566172793A204163636570742D456E636F64696E670D0A436F6E74656E742D456E636F64696E673A20677A69700D0A4B6565702D416C6976653A2074696D656F75743D31302C206D61783D31303030300D0A436F6E6E656374696F6E3A204B6565702D416C6976650D0A5472616E736665722D456E636F64696E673A206368756E6B65640D0A436F6E74656E742D547970653A20746578742F706C61696E0D0A436F6E74656E742D4C616E67756167653A20656E2D55530D0A0D0A3137640D0A1F8B0800000000000003BC514D4B024118FE2F73DEC3ECF84178D54B9497EA161E361D6271BFD85D0F220BDAC99214D2B44FD240EB104514226AFD1A67D4937FA1777083EA10154B737ADFE77DE679DE67A6806CEAE66C236E66288AA14D24F940D2D9851EDA9C436D142BA0B462DBAA2811C62B188E1C21304E9BBAA51879FFFE3A26110055274135EAD2A542D61FAEC110CB32C65100757347D57C82A1E8A2E20FC7FCA43A2D9EF9AEAB1900E51021241C8E46E5B00F6FE52DC1C604791202116D83EA8A9D4D2A9658530082E18806B62428B65D402A4861114D30E3A6E152C3058D79E795DD5FCF7A25FEB8C76ABDE9B087DE496B342FDC65005CDF5088C10A564671694211E98C9CA679D28FE4F95D87B7BA81CA4F467536ACF3567BF67C13A8F0ACD4983E8D2683C3C9E06099E10FF229498C42DF3D3FBBACF18B21AF1CF1467F312E43CD4E6F59F58A9DB717E3FDAF9EE49367E8779158F7E5A3D532D53F5ACD8B45F8A5A0AD78A5291EADD6E4FD7220A9529EE7BD010000FFFF0D0A610D0A030047C60861130400000D0A300D0A0D0A";
	
	/**
	 * 数据解压缩
	 * 
	 * @param is
	 * @param os
	 * @throws Exception
	 */
	public static String decompress(InputStream is)throws Exception {
		OutputStream os = new ByteArrayOutputStream();
		GZIPInputStream gis = new GZIPInputStream(is);
		
		int count;
		byte data[] = new byte[1024];
		while ((count = gis.read(data, 0, 1024)) != -1) {
			os.write(data, 0, count);
		}

		gis.close();
		
		return os.toString();
	}
	
	// 压缩   
	 public static byte[] compress(String str) throws IOException {   
	    if (str == null || str.length() == 0) {   
	     return null;   
	   }   
	    ByteArrayOutputStream out = new ByteArrayOutputStream();   
	   GZIPOutputStream gzip = new GZIPOutputStream(out);   
	    gzip.write(str.getBytes());   
	    gzip.close();   
	   return out.toByteArray();   
	  }   
	
	
	// 解压缩   
	 public static String uncompress(String str) throws IOException {   
	    if (str == null || str.length() == 0) {   
	      return str;   
	  }   
	   ByteArrayOutputStream out = new ByteArrayOutputStream();   
	   ByteArrayInputStream in = new ByteArrayInputStream(str   
	        .getBytes("ISO-8859-1"));   
	    GZIPInputStream gunzip = new GZIPInputStream(in);   
	    byte[] buffer = new byte[256];   
	    int n;   
	   while ((n = gunzip.read(buffer))>= 0) {   
	    out.write(buffer, 0, n);   
	    }   
	    // toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)   
	    return out.toString();   
	  }   
	
	public static void main(String[] args){
		
		try {
			byte[] tt = compress("{\"versionName\":\"1.0.6\",\"downloadurl\":\"http://lmws.cnsuning.com/lmwsdownload/lmwsclient.apk\",\"version\":\"6\"}");
			System.out.println(ByteUtil.bytes2hex(tt)+"\r\n"+uncompress(new String(tt,"ISO-8859-1")));
			
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		byte[] mre = ByteUtil.ascii2hex(hstr.getBytes());
		boolean end_head = false;
		int i =0,of = 0,endd=0;
		int len = 0;
		for(i=0;i<mre.length;i++){
			if(mre[i]==0x0d&&mre[i+1]==0x0a&&mre[i+2]==0x0d){/*找到内容头*/
				of = i+4;
				i+=4;
				end_head = true;
				System.out.println("deal:"+of);
			}
			if(end_head){
				if(mre[i]==0x0d&&mre[i+1]==0x0a){
					of = i+2;
					i+=2;
					System.out.println("head:"+of);
					end_head = false;
					len = mre.length-of-7-8-11;
					break;
				}
			}
			if(mre[i]==0x00&&mre[i+1]==0x00&&mre[i+2]==0xff&&mre[i+3]==0xff){
				System.out.println("end:"+of);
				len = i - of;
				endd = i+11;
				break;
			}
		}
		byte[] heads = ByteUtil.sub(mre, 0, i);
		
		String head = new String(heads);
		String[] params = head.split("\r\n");
		for(int l=0;l<params.length;l++){
			if(params[l].contains("gzip")){
				params[l] = "";/*替换头*/
			}
		}
		head = "";
		for(int l=0;l<params.length;l++){
			if(params[l].length()>0){
				head = head + params[l]+"\r\n";
				System.out.println(params[l]);
			}
		}
		head = head + "\r\n";
		System.out.println(of+"-"+len);
		byte[] b_zip =ByteUtil.union(ByteUtil.sub(mre, of, len),ByteUtil.sub(mre, mre.length-15, 8));
		if(b_zip[9]==0x03)b_zip[10]=(byte) (b_zip[10]+1);
		
		System.out.println(ByteUtil.bytes2hex(b_zip));

		String re_txt = null;
		try {
			re_txt = uncompress(new String(b_zip,"ISO-8859-1"));
			System.out.println(re_txt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
