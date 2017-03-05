public class BinarySearchTest
{
	public static void main(String [] args)
	{
		int value = 18;
		int [] array = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
		int index = binarySearch(array,value);
		System.out.println(index == -1?"数组中没有查询到这个元素":"元素在数组中的索引为: "+ index);
	}

	public static int binarySearch(int [] array, int value)
	{
		int low = 0; //最开始数组第一个元素的下标
		int high = array.length -1; //最开始数组最后一个元素的下标
		int middle; //需要和查找的值比较的值的下标索引

		while(low <= high)
		{
			middle = (low + high) / 2; //如果是奇数除不尽带小数点怎么办？
			
			if(array[middle] == value)
			{
				return middle;
			}

			if(array[middle] > value)
			{
				high = middle - 1;
			}
			
			if(array[middle] < value)
			{
				low = middle + 1;
			}
			
		}	
		return -1;
	}
}
