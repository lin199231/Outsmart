package findix.meetingreminder.segmentation;

import java.io.*;
import java.util.*;

public class GetHash {
	public GetHash() {
	}

	public int[] getHashCode(String str) {
		int HashCode[] = new int[3];
		HashCode[0] = (int) hashBrief(DJBHash(str), 5000011);
		HashCode[1] = (int) hashBrief(DEKHash(str), 5000011);
		HashCode[2] = (int) hashBrief(RSHash(str), 5000011);
		return HashCode;
	}

	@SuppressWarnings("resource")
	public void getHashTable(int Mode) {
		String str;
		File main2012 = new File(
				"C:\\Users\\feng\\workspace\\Bloom\\main2012-5.txt");
		// File RSHash=new
		// File("C:\\Users\\feng\\workspace\\Bloom\\RSHash.txt");
		// File BKDRHash=new
		// File("C:\\Users\\feng\\workspace\\Bloom\\BKDRHash.txt");
		// File SDBMHash=new
		// File("C:\\Users\\feng\\workspace\\Bloom\\SDBMHash.txt");
		// File DJBHash=new
		// File("C:\\Users\\feng\\workspace\\Bloom\\DJBHash.txt");
		// File DEKHash=new
		// File("C:\\Users\\feng\\workspace\\Bloom\\DEKHash.txt");
		File MKHash = new File("C:\\Users\\feng\\workspace\\Bloom\\MKHash.txt");
		try {
			// RSHash.createNewFile();
			// System.out.println("创建RS");
			// BKDRHash.createNewFile();
			// System.out.println("创建BKDR");
			// SDBMHash.createNewFile();
			// System.out.println("创建SDMB");
			// DJBHash.createNewFile();
			// System.out.println("创建DJB");
			// DEKHash.createNewFile();
			// System.out.println("创建DEK");
			BufferedReader br = new BufferedReader(new FileReader(main2012));
			// BufferedWriter RS=new BufferedWriter(new FileWriter(RSHash));
			// BufferedWriter BKDR=new BufferedWriter(new FileWriter(BKDRHash));
			// BufferedWriter SDBM=new BufferedWriter(new FileWriter(SDBMHash));
			// BufferedWriter DJB=new BufferedWriter(new FileWriter(DJBHash));
			// BufferedWriter DEK=new BufferedWriter(new FileWriter(DEKHash));
			BufferedWriter MK = new BufferedWriter(new FileWriter(MKHash));
			while ((str = br.readLine()) != null) {
				// RS.write(RSHash(str)+"");
				// RS.newLine();
				// BKDR.write(BKDRHash(str)+"");
				// BKDR.newLine();
				// SDBM.write(SDBMHash(str)+"");
				// SDBM.newLine();
				// DJB.write(DJBHash(str)+"");
				// DJB.newLine();
				// DEK.write(DEKHash(str)+"");
				// DEK.newLine();
				MK.write(DEKHash(str) + "");
				MK.newLine();
			}
			// System.out.println("写入成功");
			// br.close();
			// System.out.println("关闭br");
			// RS.close();
			// System.out.println("关闭RS");
			// BKDR.close();
			// System.out.println("关闭BKDR");
			// SDBM.close();
			// System.out.println("关闭SDBM");
			// DJB.close();
			// System.out.println("关闭DJB");
			// DEK.close();
			// System.out.println("关闭DEK");
			MK.close();
			System.out.println("关闭MK");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double testHash(int mode) {
		double p;
		String str;
		ArrayList<Long> hashTable = new ArrayList<Long>();
		File main2012 = new File(
				"C:\\Users\\feng\\workspace\\Bloom\\main2012-5.txt");
		try {
			BufferedReader br = new BufferedReader(new FileReader(main2012));
			// for (int i = 0; i < 200000; i++) {
			// if (i % 1000 == 0) {
			// for (int j = 0; j < 4; j++) {
			// str = br.readLine();
			// hashTable.add(hashMode(str, mode));
			// }
			// } else
			// br.readLine();
			// }
			while ((str = br.readLine()) != null) {
				hashTable.add(hashMode(str, mode));
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int odd = 0;
		for (int i = 0; i < hashTable.size(); i++) {
			while ((odd = hashTable.indexOf(hashTable.get(i))) != -1
					&& odd != i) {
				hashTable.remove(odd);
			}
		}
		System.out.println(hashTable.size());
		p = (275714.0 - hashTable.size()) / 275714;
		return p;
	}

	private long hashMode(String str, int mode) {
		switch (mode) {
		case 1:
			return RSHash(str);
		case 2:
			return BKDRHash(str);
		case 3:
			return SDBMHash(str);
		case 4:
			return DJBHash(str);
		case 5:
			return DEKHash(str);
		case 6:
			return MKHash(str);
		default:
			return -1;
		}
	}

	// 1
	// 从Robert Sedgwicks的 Algorithms in C一书中得到了。
	// 我(原文作者)已经添加了一些简单的优化的算法，以加快其散列过程。
	public long RSHash(String str) {
		int b = 378551;
		int a = 63689;
		long hash = 0;
		for (int i = 0; i < str.length(); i++) {
			hash = hash * a + str.charAt(i);
			a = a * b;
		}
		return hash;
	}

	// 2
	// 这个算法来自Brian Kernighan 和 Dennis Ritchie的 The C Programming Language。
	// 这是一个很简单的哈希算法,使用了一系列奇怪的数字,
	// 形式如31,3131,31...31,看上去和DJB算法很相似。
	public long BKDRHash(String str) {
		long seed = 131; // 31 131 1313 13131 131313 etc..
		long hash = 0;
		for (int i = 0; i < str.length(); i++) {
			hash = (hash * seed) + str.charAt(i);
		}
		return hash;
	}

	// 3
	// 这个算法在开源的SDBM中使用，似乎对很多不同类型的数据都能得到不错的分布
	public long SDBMHash(String str) {
		long hash = 0;
		for (int i = 0; i < str.length(); i++) {
			hash = str.charAt(i) + (hash << 6) + (hash << 16) - hash;
		}
		return hash;
	}

	// 4
	// 这个算法是Daniel J.Bernstein 教授发明的，是目前公布的最有效的哈希函数。
	public long DJBHash(String str) {
		long hash = 5381;
		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) + hash) + str.charAt(i);
		}
		return hash;
	}

	// 5
	// 由伟大的Knuth在《编程的艺术 第三卷》的第六章排序和搜索中给出。
	public long DEKHash(String str) {
		long hash = str.length();
		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) ^ (hash >> 27)) ^ str.charAt(i);
		}
		return hash;
	}

	// 6
	// 由伟大的MK将4和5组合而创造的哈希函数
	public long MKHash(String str) {
		long hash = 5381;
		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) + (hash >> 2)) + str.charAt(i);
		}
		return hash;
	}

	// private int Mode = 0;
	public long hashBrief(long hash, int Prime) {
		long Bhash = 0;
		Bhash = hash % Prime;
		return Math.abs(Bhash);
	}
}
