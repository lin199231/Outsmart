package findix.meetingreminder.segmentation;

import java.io.*;
import java.util.*;

public class GetHash {
	public GetHash() {
	}

	public int[] getHashCode(String str) {
		int HashCode[] = new int[3];
		HashCode[0] = (int) hashBrief(hashMode(str,4), 5000011);
		HashCode[1] = (int) hashBrief(hashMode(str,5), 5000011);
		HashCode[2] = (int) hashBrief(hashMode(str,1), 5000011);
		return HashCode;
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
