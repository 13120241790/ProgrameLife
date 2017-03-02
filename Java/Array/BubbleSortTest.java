public class BubbleSortTest
{
	public static void main(String [] args)
	{
		int [] arrays = {6,2,3,1,7,9,8};
	//	SortUtils.arrayBubbleSort(arrays);
		arrayBubbleSort(arrays);
		for(int i = 0; i < arrays.length ; i++)
		{
			System.out.println(arrays[i]);
		}
	}
	//升序
	public static int[] arrayBubbleSort(int[] array)
	{		
		if(array == null)
		{
			return null;
		}
		if(array.length == 1 || array.length == 0)
		{
			return array;
		}
		for(int i =0; i < array.length -1 ; i++)
		{
			for(int j =0; j < array.length -i -1 ; j++)
			{
			if(array[j] > array[j+1])
			{
				int temp = array[j];
				array[j] = array[j+1];
				array[j+1] = temp;
				 
			}
			}
		}
		return array;
	}

}

//自己写的错误代码
class SortUtils
{
	public static int[] arrayBubbleSort(int[] array)
	{
		if(array == null)
		{
			return null;
		}
		if(array.length == 1 || array.length == 0)
		{
			return array;
		}
		int jumpCount = 0;
		while(true)
		{
		for(int i=0; i < array.length; i++)
		{
			if(i+1  == array.length)
			{
				break;
			}
			if(array[i] > array[i+1])
			{
				int temp = array[i];
				array[i] = array[i+1];
				array[i+1] = temp;
				 
			}else{
				jumpCount++;
			}
		}
			if(jumpCount == array.length -1)
			{
				break;
			}
		}
		return array;
	}

}
