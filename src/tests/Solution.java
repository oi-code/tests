package tests;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class Solution {

    public static final String home = System.getProperty("user.home");

    public static void main(String... args) throws Exception {

	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {
	}

	// new BinarySearch().recursionSearch(55, 0, 15);
	// new TreeApp().main(new String[] {});
	// new AppletViewer().init();
	// new Random().ints(Integer.MAX_VALUE / 2,
	// Integer.MAX_VALUE).limit(50).forEach(i -> System.out.println(i % 60));
	// new RealHashKarta();
	// new AfterPDFtoFB2Convertor();
	// new DrawFromCodedImage().init();
	// Ugh.init();
	// new ReplaceinGCode().init();
	// new MergeSort();
	// new ShellSort();
	// new QuickSort();
	// new FrameTest().cheburek();
	// new ExtentTest_ext(2);	
	
    }

}


class ExtentTest{
    
    ExtentTest(){
	kek();
    }
    
    void kek(){
	System.out.println("kek");
    }
}

class ExtentTest_ext extends ExtentTest{
    int i;
    ExtentTest_ext(int q){
	i=q;
    }
    void kek() {
	System.out.println("cheburek " +i);
    }
}

class FrameTest {
    private List<String> arr;

    public void kek() {
	System.out.println(arr);
    }

    /*
     * that you can create multithread class with wait()/notifyAll()
     * like
     * 
     * synchronized(arr){
     * while(arr==null){
     * this.wait();
     * }
     * 
     */
    public void cheburek() {
	kek();
	List<String> ara = new ArrayList<String>();
	ara.add("kek");
	kek();
	arr = ara;
	/*
	 * now use notifyAll() and check, that arr!=null
	 * at once fater arr=ara use
	 * 
	 * synchronized(arr){
	 * arr.notifyAll();
	 * }
	 */
	kek();
    }
}

class QuickSort {

    private int[] arr;
    static int recLevel = 0;

    public QuickSort() {
	arr = ThreadLocalRandom.current().ints(0, 1000).limit(100).toArray();
	System.out.println(Arrays.toString(arr));
	quickSort();
	System.out.println(Arrays.toString(arr));
    }

    private void quickSort() {
	/**
	 * Инициализация.
	 */
	recQuickSort(0, arr.length - 1);
	/**
	 * Это ещё один вариант, который советуют использовать.
	 * Однако, это может вызвать падение производительности, так как одна
	 * большая сортировка методом вставок может быть медленнее множества мелких
	 * из-за расположения данных в массиве.
	 * 
	 * Конфигурируется данный способ так:
	 * 
	 * Закоментировать вызов insertSort в методе recQuickSort в первом блоке
	 * ветвления с проверкой размера для первого способа.
	 * 
	 * Закоментировать вызов insertSort прямо тут и расскоментировать вызов в первом
	 * блоке ветвления с проверкой размера для первого способа.
	 */
	insertSort(0, arr.length - 1);
	check();
    }

    private void recQuickSort(int left, int right) {
	recLevel++;
	/**
	 * Условие выхода.
	 */
	int size = right - left + 1;
	// Для обычной сортировки с опорным значением в arr[right]
	// if (right - left <= 0) {// return;
	if (size <= 9) {
	    /**
	     * manualSort - простая версия. Для его работы нужен size<=3, обычная версия.
	     *
	     * insertSort - улучшенная версия, метод вставки. Работает лучше при размере
	     * size<=9. Значение подбирается тестами и на разном железе отрабатывает
	     * по-разному.
	     * 
	     * Выбор между manualSort и insertSort определяется по тестам. На одних данных
	     * они могут выдавать разную производительность, в любую сторону.
	     * 
	     * Третий способ описан в инициализации.
	     */
	    // manualSort(left, right);
	    // insertSort(left, right);
	} else {
	    /**
	     * Выбирается крайний ПРАВЫЙ элемент.
	     */
	    int temp = getMedian(left, right);// arr[right];
	    /**
	     * Из массива выбирается опорный элемент, на который находится в крайнем правом
	     * положении.
	     */
	    int index = delimit(left, right, temp);
	    /**
	     * Сортировка левой половины.
	     */
	    recQuickSort(left, index - 1);
	    /**
	     * Сортировка правой половины.
	     */
	    recQuickSort(index + 1, right);
	}
    }

    /**
     * Метод обмена значений. То-же самое лучше писать прямо в коде, это не будет
     * создавать дополнительные затраты на создание и вызов метода.
     */
    private void swap(int left, int right) {
	int temp = arr[left];
	arr[left] = arr[right];
	arr[right] = temp;
    }

    /**
     * Ручная сортировка массива, если его размер меньше 4 элементов.
     */
    private void manualSort(int left, int right) {
	int size = right - left + 1;
	/**
	 * Если размер =1, сортировка окончена.
	 */
	if (size <= 1) {
	    return;
	}
	/**
	 * Сортировка 2х элементов.
	 */
	if (size == 2) {
	    if (arr[left] > arr[right]) {
		swap(left, right);
	    }
	    return;
	    /**
	     * Сортировка 3х элементов.
	     */
	} else {
	    if (arr[left] > arr[right - 1]) {
		swap(left, right - 1);
	    }
	    if (arr[left] > arr[right]) {
		swap(left, right);
	    }
	    if (arr[right - 1] > arr[right]) {
		swap(right - 1, right);
	    }
	}
    }

    /**
     * Обычная сортировка методом вставки.
     */
    private void insertSort(int left, int right) {
	int leftIndex, rightIndex;
	for (leftIndex = left + 1; leftIndex <= right; leftIndex++) {
	    /**
	     * Сохранение значения для поиска места.
	     */
	    int temp = arr[leftIndex];
	    rightIndex = leftIndex;
	    /**
	     * Перебор всех элементов СПРАВА на ЛЕВО. Все элементы смещаются на 1 позицию
	     * вправо до тех пор, покуда temp не будет равен или меньше указанного значения.
	     * После элемент temp вставляется в массив и процесс повторяется снова до конца
	     * цикла for.
	     */
	    while (rightIndex > left && arr[rightIndex - 1] >= temp) {
		arr[rightIndex] = arr[rightIndex - 1];
		rightIndex--;
	    }
	    /**
	     * Сама вставка элемента.
	     */
	    arr[rightIndex] = temp;
	}
    }

    /**
     * Выбор среднего значения по трём точкам. Помогает избежать проблем, если
     * массив отсортирован в обратном порядке.
     */
    private int getMedian(int left, int right) {
	int mid = (right + left) / 2;
	if (arr[left] > arr[mid]) {
	    swap(left, mid);
	}
	if (arr[left] > arr[right]) {
	    swap(left, right);
	}
	if (arr[mid] > arr[right]) {
	    swap(mid, right);
	}
	swap(mid, right - 1);
	return arr[right - 1];
    }

    private int delimit(int left, int right, int temp) {
	/**
	 * left - первый элемент массива.(лучше поменять на head)
	 * right - последний элемент массива. (лучше поменять на tail)
	 * leftIndex - входная точка алгоритма с левой части. (лучше поменять на headEntrance)
	 * rightIndex - входная точка алгоритма с правой части. (лучше поменять на tailEntrance)
	 * temp - временное значение переменной для переноса в другую часть.
	 */
	int leftIndex = left;// - 1;
	int rightIndex = right - 1;
	/**
	 * Основной алгоритм.
	 */
	while (true) {
	    /**
	     * Перемещение левого leftIndex индекса до элемента temp.
	     */
	    while (/* leftIndex < right && */ arr[++leftIndex] < temp) {

	    }
	    /**
	     * Перемещение правого rightIndex индекса до элемента temp.
	     */
	    while (/* rightIndex > left && */ arr[--rightIndex] > temp) {

	    }
	    /**
	     * Закоментированый код выше просто для ускорения. Нужно раскоментировать, если
	     * сортировка будет вестись с выбором в качестве опорного элемента arr[right].
	     * 
	     * Если индексы перешли друг через друга - выходная точка алгоритма.
	     */
	    if (leftIndex >= rightIndex) {
		break;
	    } else {
		/**
		 * Перемещение элемента из одной части массива в другую. В данном случае,
		 * элементы меньше чем temp - вправо, больше - влево. Меняются местами именно
		 * leftIndex и rightIndex! Это важно!
		 */
		/*
		 * int temp_ = arr[leftIndex];
		 * arr[leftIndex] = arr[rightIndex];
		 * arr[rightIndex] = temp_;
		 */
		swap(leftIndex, rightIndex);
	    }
	}
	/**
	 * На последнем шаге алгоритма элементы ИМЕННО В leftIndex и right не на своих
	 * местах, и их нужно поменять друг с другом.
	 */
	/*
	 * Код для сортировки с выбором в качестве опорного элемента arr[right]
	 * int temp_ = arr[leftIndex];
	 * arr[leftIndex] = arr[right];
	 * arr[right] = temp_;
	 */
	swap(leftIndex, right - 1);
	/**
	 * Возврат индекса. В нём находится temp и в дальнейшей сортировке не участвует.
	 */
	return leftIndex;
    }

    private void check() {
	int check = arr[0];
	boolean yea = true;
	for (int i = 1; i < arr.length - 1; i++) {
	    if (arr[i] >= check) {
		check = arr[i];
	    } else {
		yea = false;
		System.err.println("Sort fail. Index: " + i);
		break;
	    }
	}
	if (yea) {
	    System.out.println("Successful.");
	}
	System.out.println("Done. Recursion level: " + recLevel);
    }
}

class ShellSort {
    private int[] arr;

    public ShellSort() {
	arr = ThreadLocalRandom.current().ints(0, 1000).limit(10).toArray();
	arr = new int[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
	System.out.println(Arrays.toString(arr));
	sort();
	System.out.println(Arrays.toString(arr));
    }

    private void sort() {
	int in, out, temp, step = 0;

	/**
	 * Вычисление шага между элементами массива. При размере массива в 10 элементов,
	 * шаг будет равен 4. Будут сортироваться подмассивы, элементы которого
	 * находятся на расстоянии в 4 шага друг от друга на первом этапе. С каждой
	 * новой итерацией, шаг будет уменьшаться, вплоть до 1. Шаг 1 - обычная
	 * сортировка вставками.
	 */

	while (step <= arr.length / 3) {
	    step = step * 3 + 1;
	}
	/**
	 * Основной цикл. step уменьшается в обратном порядке.
	 * out - это количество шагов в n сортировке. Цикл
	 * for пройдёт 4 раза, т.к step из цикла выше будет равен 4.
	 */
	while (step > 0) {
	    /**
	     * При размере массива 10, цикл for выполнится 6 раз, начиная с 4 элемента.
	     */
	    for (out = step; out < arr.length; out++) {
		temp = arr[out];
		in = out;
		System.out.printf("in for:\s\s\sout %d,\sin %d,\sstep %d,\stemp %d%n", out, in, step, temp);
		/**
		 * До тех пор, покуда in = 4 больше чем step-1=3 и число в массиве in-step
		 * больше
		 * числа, сохранённого в temp, будет выполняться цикл while с уменьшением in на
		 * step и перестановкой элемента temp в позицию в массиве in-step.
		 */
		while (in > step - 1 && arr[in - step] >= temp) {
		    arr[in] = arr[in - step];
		    System.err.printf("in while: out %d,\sin %d,\sstep %d,\sarr[in-step] %d%n", out, in, step,
			    arr[in - step]);
		    in -= step;
		}
		/**
		 * Когда цикл завершит работу, в массиве будет пустое место, в которое и
		 * поместиться число из temp и оно уже будет отсортировано ОТНОСИТЕЛЬНО двух
		 * других чисел в позициях между step=4.
		 */
		arr[in] = temp;
		/**
		 * Далее идёт повторение цикла for и увеличение значения out на +1. Цикл
		 * продолжится с 5 элемента и т.д.
		 */
	    }
	    /**
	     * Уменьшение шага. Следующий размер шага будет 1, полный перебор массива через
	     * 1 элемент.
	     */
	    step = (step - 1) / 3;
	}
    }
}

class MergeSort {

    private int[] arr;

    public MergeSort() {
	arr = ThreadLocalRandom.current().ints(0, 100000).limit(10).toArray();
	arr = new int[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
	System.out.println(Arrays.toString(arr));
	sort();
	System.out.println(Arrays.toString(arr));
    }

    private void sort() {
	int[] temp = new int[arr.length];
	recMergeSort(temp, 0, arr.length - 1);
    }

    private void recMergeSort(int[] temp, int low, int up) {
	if (low == up) {
	    return;
	} else {
	    int mid = (low + up) / 2;
	    recMergeSort(temp, low, mid);
	    recMergeSort(temp, mid + 1, up);
	    merge(temp, low, mid + 1, up);
	}
    }

    private void merge(int[] temp, int low, int mid, int up) {

	int step = 0;
	int finalLow = low;
	int center = mid - 1;
	int end = up - finalLow + 1;

	while (low <= center && mid <= up) {
	    if (arr[low] < arr[mid]) {
		temp[step++] = arr[low++];
	    } else {
		temp[step++] = arr[mid++];
	    }
	}
	while (low <= center) {
	    temp[step++] = arr[low++];
	}
	while (mid <= up) {
	    temp[step++] = arr[mid++];
	}
	for (step = 0; step < end; step++) {
	    arr[step + finalLow] = temp[step];
	}

	/**
	 * Основной цикл. Выполнится меньшее из условий.
	 */

	/*
	 * while(low<=center&&mid<=up) {
	 * if(arr[low]<arr[mid]) {
	 * temp[step++]=arr[low++];
	 * }
	 * else {
	 * temp[step++]=arr[mid++];
	 * }
	 * }
	 * System.out.println("1: tempArr:\t"+Arrays.toString(temp));
	 * System.out.println("1: arr:\s\s\s\s\t"+Arrays.toString(arr));
	 * System.out.println();
	 */

	/**
	 * Копирование оставшегося массива из левой части до середины
	 * Сработает в том случае, если левая часть оказалась больше правой
	 * в первом while цикле.
	 */

	/*
	 * while(low<=center) {
	 * temp[step++]=arr[low++];
	 * }
	 * 
	 * System.out.println("2: tempArr:\t"+Arrays.toString(temp));
	 * System.out.println("2: arr:\s\s\s\s\t"+Arrays.toString(arr));
	 * System.out.println();
	 */

	/**
	 * Копирование оставшегося массива из середины до конца
	 * Сработает в том случае, если правая часть массива оказалась больше
	 * в первом while цикле.
	 */
	/*
	 * while(mid<=up) {
	 * temp[step++]=arr[mid++];
	 * }
	 */

	// System.out.println("3: tempArr:\t"+Arrays.toString(temp));
	// System.out.println("3: arr:\s\s\s\s\t"+Arrays.toString(arr));

	/**
	 * Просто перезапись сортированных данных из временного массива в основной.
	 */

	/*
	 * for(step=0;step<end;step++) {
	 * arr[finalLow+step]=temp[step];
	 * }
	 * 
	 * System.err.println("4: exit\n");
	 */
    }
}

@SuppressWarnings("serial")
class ReplaceinGCode extends JFrame {
    JFileChooser jFileChooser = new JFileChooser(Solution.home + "\\Desktop");
    FileNameExtensionFilter filter = new FileNameExtensionFilter(".gcode", "gcode", "gcode");
    JTextArea jText = new JTextArea();
    JButton jbutton = new JButton();
    StringBuilder sb = new StringBuilder();
    JPanel jPanel = new JPanel();
    SimpleAttributeSet attribs = new SimpleAttributeSet();
    Path p;
    static String s;
    static {
	try {
	    s = JOptionPane.showInputDialog(null, "Replace \'M3 S255\' with another.\nInput new angle for M3 command:",
		    "M3 command changer", JOptionPane.INFORMATION_MESSAGE).toUpperCase();

	} catch (NullPointerException e) {
	    JOptionPane.showMessageDialog(null, "You canceled operation. Exit...", "Canceled",
		    JOptionPane.INFORMATION_MESSAGE);
	    System.exit(0);
	}
    }

    public void init() {

	jbutton.setText("Press me for choose file");
	jbutton.setFocusable(false);
	StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
	StyleConstants.setFontSize(attribs, 20);
	StyleConstants.setBold(attribs, true);
	jText.setText("\n\t\tChoose .gcode file:");
	jText.setLineWrap(true);
	jText.setWrapStyleWord(true);
	// jText.setParagraphAttributes(attribs, true);
	jText.setFont(jText.getFont().deriveFont(15f));
	jText.setEditable(false);
	jText.setFocusable(false);

	jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	jFileChooser.setFileFilter(filter);
	jFileChooser.setDialogTitle("choosing \".gcode\" file...");

	Matcher mat = Pattern.compile("S\\d+").matcher(s);

	if (mat.find()) {
	    int confirm = JOptionPane.showConfirmDialog(null, "M3 S255 will be replace to M3 " + s,
		    "M3 command changer", JOptionPane.YES_NO_OPTION);
	    if (confirm == JOptionPane.NO_OPTION) {
		JOptionPane.showMessageDialog(null, "You canceled operation. Exit...", "Canceled",
			JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	    }
	} else {
	    JOptionPane.showMessageDialog(null, "ERROR, break data: " + s, "ERROR", JOptionPane.ERROR_MESSAGE);
	    System.exit(0);
	}
	System.out.println(s);
	jbutton.addActionListener(e -> {
	    int result = jFileChooser.showOpenDialog(null);
	    if (result == JFileChooser.APPROVE_OPTION) {
		p = Paths.get(jFileChooser.getSelectedFile().getAbsolutePath());
		try {
		    Files.lines(p).forEach(line -> {
			if (line.equals("M3 S255;")) {
			    sb.append(String.format("M3 %s;\n", s));
			} else {
			    sb.append(line + "\n");
			}
		    });
		    Path temp = Paths.get(Solution.home + "\\Desktop\\"
			    + p.getFileName().toString().substring(0, p.getFileName().toString().lastIndexOf('.'))
			    + "_fixed.gcode");
		    // Files.writeString(temp, sb);
		    Files.write(temp, sb.toString().getBytes());
		    jText.setText("File selected and modified. Press button again for chose new file. File name: "
			    + temp.getFileName());
		    jText.setBackground(new Color(204, 255, 0));
		} catch (Exception e1) {
		    jText.setBackground(Color.RED);
		    jText.setText("\n\s\sERROR during read/write file! Chose another file!");
		}
	    }
	});
	GridLayout gr = new GridLayout(2, 1, 1, 1);

	jPanel.setLayout(gr);
	jPanel.add(jText);
	jPanel.add(jbutton);
	add(jPanel);
	setResizable(false);
	// getContentPane().add(jText, gr);
	// getContentPane().add(jbutton, gr);
	setTitle("M3 command changer. M3 S255 to M3 " + s);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setSize(500, 200);
	setLocationRelativeTo(null);
	setVisible(true);
    }
}

class Ugh {
    static void init() throws IOException {
	Path path = Paths.get(Solution.home + "\\Desktop\\jpwh-2e-examples-20151103");
	Set<String> paths = new HashSet<String>();
	Files.walkFileTree(path, new FileVisitor<Path>() {

	    @Override
	    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

		/*
		 * BufferedReader in = new BufferedReader(new FileReader(file.toFile()));
		 * while (in.ready()) {
		 * String line = in.readLine();
		 * if
		 * (line.toLowerCase().contains("bitronix")||line.toLowerCase().contains("btm"))
		 * {
		 * paths.add(file.toString());
		 * }
		 * in.close();
		 * }
		 */
		Files.lines(path).filter(e -> e.toLowerCase().contains("bitronix")).forEach(e -> paths.add(e));
		return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	    }
	});
	for (String s : paths) {
	    System.out.println(s);
	}
    }
}

class DrawFromCodedImage {

    public void init() {
	new Thread(() -> {
	    @SuppressWarnings("serial")
	    class Show extends JFrame {
		StringBuilder sb = new StringBuilder();
		{
		    sb.append("FIRST_LINE\n");
		    sb.append("SECOND_LINE\n");
		    sb.append("THIRD_LINE\n");
		    sb.append("FOURTH_LINE");
		}

		@Override
		public void paint(Graphics g) {
		    super.paint(g);

		    String[] arr = sb.toString().split("\n");
		    Graphics2D g2d = (Graphics2D) g;
		    for (int i = 0; i < arr.length; i++) {
			new TextLayout(arr[i], new Font("", Font.BOLD, 12), new FontRenderContext(null, false, false))
				.draw(g2d, 10, 560 + i * 10);
		    }
		    /*
		     * TextLayout jlb = new TextLayout(arr[0], new Font("", Font.BOLD, 12),
		     * new FontRenderContext(null, false, false));
		     * TextLayout jlb1 = new TextLayout(arr[1], new Font("", Font.BOLD, 12),
		     * new FontRenderContext(null, false, false));
		     * TextLayout jlb2 = new TextLayout(arr[2], new Font("", Font.BOLD, 12),
		     * new FontRenderContext(null, false, false));
		     * jlb.draw(g2d, 10, 570);
		     * jlb1.draw(g2d, 20, 580);
		     * jlb2.draw(g2d, 20, 590);
		     */

		}

		public void init() {
		    try {
			java.nio.file.Path in = Paths.get(Solution.home + "\\Desktop\\photo-256.png");
			java.nio.file.Path out = Paths.get(Solution.home + "\\Desktop\\qq.txt");
			byte[] coded = new byte[(int) in.toFile().length()];
			FileInputStream fis = new FileInputStream(in.toFile());
			fis.read(coded);
			fis.close();

			String codedImage = Base64.getEncoder().encodeToString(coded);
			Files.write(out, codedImage.getBytes());

			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setBounds(200, 200, 400, 610);
			fis = new FileInputStream(out.toFile());
			byte[] decoded = new byte[(int) out.toFile().length()];
			fis.read(decoded);
			ImageIcon icon = new ImageIcon(Base64.getDecoder().decode(decoded));
			this.getContentPane().add(new JLabel(icon));
			this.setVisible(true);
		    } catch (Exception e) {
			System.out.println("EEEEERROOOORRRR AAAA");
		    }
		}
	    }
	    new Show().init();
	}).start();
    }
}

/*
 * class Pars {
 * private static StringBuilder sb = new StringBuilder();
 * private static String mainurl = "";
 * public static void main(String... args) {
 * try {
 * List<String> links = new ArrayList<String>();
 * Document doc = Jsoup.connect("").userAgent("Chrome/81.0.4044.138").get();
 * Elements text = doc.getElementsByTag("a");
 * for (Element el : text) {
 * String aa = el.attr("href");
 * if (aa.contains("books/")) {
 * links.add(mainurl + aa);
 * }
 * }
 * System.out.println(links);
 * new Pars().worker(links);
 * } catch (Exception e) {
 * e.printStackTrace();
 * }
 * try {
 * Files.write(Paths.get("C:\\Users\\*\\Desktop\\res.txt"),
 * sb.toString().getBytes());
 * } catch (IOException e) {
 * e.printStackTrace();
 * }
 * }
 * private void worker(List<String> ls) {
 * 
 * try {
 * //for (String s : ls) {
 * String s=ls.get(10);
 * Document doc = Jsoup.connect(s).userAgent("Chrome/81.0.4044.138").get();
 * Elements elem = doc.getElementsByTag("code");
 * AtomicInteger count = new AtomicInteger(0);
 * // StringBuilder sb = new StringBuilder();
 * String[] code =elem.text().split("~");
 * System.err.println("-------->"+code.length);
 * //for (int i = 0; i < code.length; i++) {
 * // System.out.println(code[i]);
 * //}
 * for (int i = 0; i < code.length; i++) {
 * code[i] = code[i] + "\n";
 * }
 * doc.body().html().lines().forEach(e -> {
 * e = e.trim();
 * if (e.startsWith("<p>")) {
 * e = e.replace("<p>", "");
 * if (e.endsWith("</p>")) {
 * e = e.replace("</p>", "");
 * }
 * sb.append(e + "\n");
 * }
 * if (e.startsWith("<li>") && !e.contains("href")) {
 * e = e.replace("<li>", "");
 * if (e.endsWith("</li>")) {
 * e = e.replace("</li>", "");
 * }
 * sb.append("\t* " + e + "\n");
 * }
 * if (e.contains("<code")) {
 * sb.append("\n" + transform(code[count.getAndIncrement()]) + "\n");
 * //sb.append("\n" + transform(elem.text()) + "\n");
 * // System.out.println(elem.text());
 * }
 * });
 * //}
 * } catch (Exception e2) {
 * e2.printStackTrace();
 * }
 * //System.out.println(sb);
 * }
 * private static String transform(String s) {
 * List<String> arr = List.of(s.split(" "));
 * String newS = "";
 * for (String as : arr) {
 * if (as.equals("//") || as.equals("//:") || as.equals("/:") ||
 * as.equals("/ :")) {
 * continue;
 * }
 * newS += as + " ";
 * }
 * System.out.println(newS);
 * String result = " ";
 * //s = s.replaceAll("//: ", "");
 * //s = s.replaceAll("// ", "");
 * BufferedReader in = null;
 * try {
 * ASFormatter formatter = new ASFormatter();
 * in = new BufferedReader(new StringReader(newS));
 * formatter.setFormattingStyle(EnumFormatStyle.JAVA);
 * try {
 * result = FormatterHelper.format(in, formatter);
 * }catch (StringIndexOutOfBoundsException e) {
 * 
 * }
 * } catch (Exception e) {
 * e.printStackTrace();
 * } finally {
 * try {
 * in.close();
 * } catch (IOException e) {
 * e.printStackTrace();
 * }
 * }
 * // StringBuilder sb = new StringBuilder();
 * // result.lines().skip(1).forEach(e -> sb.append(e + "\n"));
 * return result;
 * }
 * }
 */

enum Perech {
    HAHA, HEHE;

    public String toString() {
	return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}

class StaticCheck {
    static {
	System.out.println("StaticA");
    }
    {
	System.out.println("nonStaticA");
    }
    static {
	System.out.println("StaticB");
    }
    {
	System.out.println("nonStaticB");
    }
    static {
	System.out.println("StaticC");
    }
    {
	System.out.println("nonStaticC");
    }

    public static void invoke() {
	System.out.println("in parent");
    }

    public String toString() {
	return "StaticCheck toString";
    }

    public String x(int a) {
	return "";
    }

}

class StaticCheck2 extends StaticCheck {
    public static void invoke() {
	System.out.println("in child");
    }

    public static void invoke(int a) {
	System.out.println("in child " + a);
    }

    public StaticCheck2() {
	System.out.println("HAHA");
    }

    public final void str() {
	System.out.println("final meth");
    }
}

class AfterPDFtoFB2Convertor {

    AfterPDFtoFB2Convertor() throws Exception {
	init();
    }

    public void init() throws IOException {
	long start = System.nanoTime();
	StringBuilder sb = new StringBuilder();
	List<Boolean> check = Arrays.asList(false);
	Path in = Paths.get("");
	Path out = Paths.get("");

	Files.lines(in).limit(199).forEach(i -> {
	    i = i.replaceAll("\\.{3,}", "...");
	    sb.append(i + "\n");
	});

	Files.lines(in).skip(199).limit(3875).forEach(i -> {
	    if (check.get(0) == false) {
		sb.append("<p>");
		check.set(0, true);
	    }
	    i = i.substring(3, i.length() - 4);
	    if (i.endsWith(".") && check.get(0) == true) {
		check.set(0, false);
		sb.append(i + "</p>\n");
		return;
	    } else {
		sb.append(i.trim());
		sb.append("\s");
	    }
	});

	Files.lines(in).skip(199 + 3875).forEach(i -> sb.append(i + "\n"));

	byte[] b = sb.toString().getBytes();
	Files.write(out, b);
	double end = System.nanoTime() - start;
	System.out.println("Done in " + (end / 1_000_000_000) + " seconds.");
    }
}

class RealHashKarta {

    private Link[] map = new Link[97];
    private int t = new Random().nextInt(1001);
    private boolean check = false;

    public RealHashKarta() {
	init();
	show();
	search(new Link(t));
    }

    private class Link {
	private int data;
	public Link next;

	Link(int d) {
	    data = d;
	}

	public int getData() {
	    return data;
	}

	public String toString() {
	    return String.valueOf(data);
	}
    }

    private void init() {
	new Random().ints(1, 1000).limit(200).forEach(i -> {
	    if (i == t) {
		check = true;
	    }
	    Link m = new Link(i);
	    int hash = hash(m);
	    if (map[hash] != null) {
		Link cur = map[hash];
		Link prev = null;
		while (cur != null) {
		    prev = cur;
		    cur = cur.next;
		}
		prev.next = m;
	    } else {
		map[hash] = m;
	    }
	});
    }

    private int hash(Link m) {
	return m.getData() % map.length;
    }

    private void show() {
	for (int i = 0; i < map.length; i++) {
	    Link cur = map[i];
	    System.out.print("Data in bucket [" + i + "]: ");
	    while (cur != null) {
		System.out.print(" " + cur);
		cur = cur.next;
	    }
	    System.out.println();
	}
    }

    private void search(Link key) {
	int hash = hash(key);
	int position = 0;
	Link temp = map[hash];
	if (temp == null) {
	    System.err.println(
		    Thread.currentThread().getStackTrace()[1].getLineNumber() + " \tCat't find key " + key + ".");
	} else {
	    while (temp != null) {
		position++;
		if (temp.getData() == key.getData()) {
		    System.err.println("\tKey " + key + " found.\n\tBucket: [" + hash + "].\n\tPosition in bucket: "
			    + position + "." + "\n\tChecked: " + check);
		    return;
		} else {
		    temp = temp.next;
		}
	    }
	    System.err.println(
		    Thread.currentThread().getStackTrace()[1].getLineNumber() + " \tCat't find key " + key + ".");
	}
	System.err.println("\tCheck: " + check);
    }
}

@SuppressWarnings("all")
class AppletViewer {

    private Path path = Paths.get("C:\\Program Files\\Java\\");
    private JFrame jFrame;
    private static Dimension size;
    private JPanel jPanel;
    private JLabel jText;
    private JFileChooser jfc;
    private JButton button;
    private String appletviewerPath;
    private String choosedFile;
    private boolean found;

    public void init() {
	jFrame = new JFrame();
	jPanel = new JPanel();
	jText = new JLabel();
	jfc = new JFileChooser();
	button = new JButton();
	appletviewerPath = "";
	choosedFile = "";
	found = false;
	size = Toolkit.getDefaultToolkit().getScreenSize();

	jFrame.setBounds((int) size.getWidth() / 2 - 550, (int) size.getHeight() / 2 - 300, 350, 200);

	jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);

	String text = "Введите путь к JavaApplet HTML файлу: ";
	jText.setText(text);
	jText.setHorizontalAlignment(JLabel.CENTER);
	jText.setFocusable(false);

	jfc.setDialogTitle("Выбор файла...");
	jfc.setCurrentDirectory(new File("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop"));
	FileNameExtensionFilter htmlFilter = new FileNameExtensionFilter("HTML files only (*.html)", "html");
	jfc.setFileFilter(htmlFilter);

	jPanel.setLayout(new GridLayout(2, 0));

	button.setFocusable(false);
	button.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		int result = jfc.showOpenDialog(jfc);
		if (result == jfc.APPROVE_OPTION) {
		    jText.setText("Выбран файл " + jfc.getSelectedFile().getName());
		    choosedFile = jfc.getSelectedFile().toString();
		    if (!choosedFile.endsWith(".html")) {
			jFrame.setTitle("Ошибка");
			jText.setText("Не верный файл. Попробуйте снова");
			return;
		    }
		    if (!appletviewerPath.equals("") && !choosedFile.equals("")) {
			String tryer = appletviewerPath + " " + choosedFile;
			try {
			    Runtime.getRuntime().exec(tryer);
			    jFrame.setTitle("Appletviewer запущен...");
			    button.setText("Выбрать новый файл...");
			} catch (IOException e1) {
			    e1.printStackTrace();
			}
		    }
		}
	    }
	});
	jPanel.add(jText);
	jPanel.add(button);
	jFrame.setTitle("Помощник запуска appletviewer");
	jFrame.add(jPanel);
	jFrame.setVisible(true);
	if (!search()) {
	    jText.setText("Не найден appletviewer. Скачайте и установите JDK 8 с официального сайта.");
	    button.setVisible(false);
	    jFrame.setTitle("Не найден appletviewer.");
	    return;
	} else {
	    button.setText("Выбрать файл");
	}
	;
    }

    private boolean search() {
	try {
	    Files.walkFileTree(path, new FileVisitor<Path>() {

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		    return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		    if (file.getFileName().toString().equals("appletviewer.exe")) {
			appletviewerPath = file.toString();
			found = true;
			return FileVisitResult.TERMINATE;
		    }
		    return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		    return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		    return FileVisitResult.CONTINUE;
		}
	    });
	} catch (Exception e) {
	    e.getStackTrace();
	}
	return found;
    }
}

class Node {
    public int iData; // data item (key)
    public double dData; // data item
    public Node leftChild; // this node's left child
    public Node rightChild; // this node's right child

    public void displayNode() // display ourself
    {
	System.out.print('{');
	System.out.print(iData);
	System.out.print(", ");
	System.out.print(dData);
	System.out.print("} ");
    }
} // end class Node
////////////////////////////////////////////////////////////////

@SuppressWarnings("all")
class Tree {
    private Node root; // first node of tree

//-------------------------------------------------------------
    public Tree() // constructor
    {
	root = null;
    } // no nodes in tree yet
//-------------------------------------------------------------

    public Node find(int key) // find node with given key
    { // (assumes non-empty tree)
	Node current = root; // start at root
	while (current.iData != key) // while no match,
	{
	    if (key < current.iData) // go left?
		current = current.leftChild;
	    else // or go right?
		current = current.rightChild;
	    if (current == null) // if no child,
		return null; // didn't find it
	}
	return current; // found it
    } // end find()
//-------------------------------------------------------------

    public void insert(int id, double dd) {
	Node newNode = new Node(); // make new node
	newNode.iData = id; // insert data
	newNode.dData = dd;
	if (root == null) // no node in root
	    root = newNode;
	else // root occupied
	{
	    Node current = root; // start at root
	    Node parent;
	    while (true) // (exits internally)
	    {
		parent = current;
		if (id < current.iData) // go left?
		{
		    current = current.leftChild;
		    if (current == null) // if end of the line,
		    { // insert on left
			parent.leftChild = newNode;
			return;
		    }
		} // end if go left
		else // or go right?
		{
		    current = current.rightChild;
		    if (current == null) // if end of the line
		    { // insert on right
			parent.rightChild = newNode;
			return;
		    }
		} // end else go right
	    } // end while
	} // end else not root
    } // end insert()
//-------------------------------------------------------------

    public boolean delete(int key) // delete node with given key
    { // (assumes non-empty list)
	Node current = root;
	Node parent = root;
	boolean isLeftChild = true;

	while (current.iData != key) // search for node
	{
	    parent = current;
	    if (key < current.iData) // go left?
	    {
		isLeftChild = true;
		current = current.leftChild;
	    } else // or go right?
	    {
		isLeftChild = false;
		current = current.rightChild;
	    }
	    if (current == null) // end of the line,
		return false; // didn't find it
	} // end while
	  // found node to delete

	// if no children, simply delete it
	if (current.leftChild == null && current.rightChild == null) {
	    if (current == root) // if root,
		root = null; // tree is empty
	    else if (isLeftChild)
		parent.leftChild = null; // disconnect
	    else // from parent
		parent.rightChild = null;
	}

	// if no right child, replace with left subtree
	else if (current.rightChild == null)
	    if (current == root)
		root = current.leftChild;
	    else if (isLeftChild)
		parent.leftChild = current.leftChild;
	    else
		parent.rightChild = current.leftChild;

	// if no left child, replace with right subtree
	else if (current.leftChild == null)
	    if (current == root)
		root = current.rightChild;
	    else if (isLeftChild)
		parent.leftChild = current.rightChild;
	    else
		parent.rightChild = current.rightChild;

	else // two children, so replace with inorder successor
	{
	    // get successor of node to delete (current)
	    Node successor = getSuccessor(current);

	    // connect parent of current to successor instead
	    if (current == root)
		root = successor;
	    else if (isLeftChild)
		parent.leftChild = successor;
	    else
		parent.rightChild = successor;

	    // connect successor to current's left child
	    successor.leftChild = current.leftChild;
	} // end else two children
	  // (successor cannot have a left child)
	return true; // success
    } // end delete()
//-------------------------------------------------------------
// returns node with next-highest value after delNode
// goes to right child, then right child's left descendents

    private Node getSuccessor(Node delNode) {
	Node successorParent = delNode;
	Node successor = delNode;
	Node current = delNode.rightChild; // go to right child
	while (current != null) // until no more
	{ // left children,
	    successorParent = successor;
	    successor = current;
	    current = current.leftChild; // go to left child
	}
	// if successor not
	if (successor != delNode.rightChild) // right child,
	{ // make connections
	    successorParent.leftChild = successor.rightChild;
	    successor.rightChild = delNode.rightChild;
	}
	return successor;
    }

//-------------------------------------------------------------
    public void traverse(int traverseType) {
	switch (traverseType) {
	case 1:
	    System.out.print("\nPreorder traversal: ");
	    preOrder(root);
	    break;
	case 2:
	    System.out.print("\nInorder traversal:  ");
	    inOrder(root);
	    break;
	case 3:
	    System.out.print("\nPostorder traversal: ");
	    postOrder(root);
	    break;
	}
	System.out.println();
    }

//-------------------------------------------------------------
    private void preOrder(Node localRoot) {
	if (localRoot != null) {
	    System.out.print(localRoot.iData + " ");
	    preOrder(localRoot.leftChild);
	    preOrder(localRoot.rightChild);
	}
    }

//-------------------------------------------------------------
    private void inOrder(Node localRoot) {
	if (localRoot != null) {
	    inOrder(localRoot.leftChild);
	    System.out.print(localRoot.iData + " ");
	    inOrder(localRoot.rightChild);
	}
    }

//-------------------------------------------------------------
    private void postOrder(Node localRoot) {
	if (localRoot != null) {
	    postOrder(localRoot.leftChild);
	    postOrder(localRoot.rightChild);
	    System.out.print(localRoot.iData + " ");
	}
    }

//-------------------------------------------------------------
    public void displayTree() {
	Stack globalStack = new Stack();
	globalStack.push(root);
	int nBlanks = 32;
	boolean isRowEmpty = false;
	System.out.println("......................................................");
	while (isRowEmpty == false) {
	    Stack localStack = new Stack();
	    isRowEmpty = true;

	    for (int j = 0; j < nBlanks; j++)
		System.out.print(' ');

	    while (globalStack.isEmpty() == false) {
		Node temp = (Node) globalStack.pop();
		if (temp != null) {
		    System.out.print(temp.iData);
		    localStack.push(temp.leftChild);
		    localStack.push(temp.rightChild);

		    if (temp.leftChild != null || temp.rightChild != null)
			isRowEmpty = false;
		} else {
		    System.out.print("--");
		    localStack.push(null);
		    localStack.push(null);
		}
		for (int j = 0; j < nBlanks * 2 - 2; j++)
		    System.out.print(' ');
	    } // end while globalStack not empty
	    System.out.println();
	    nBlanks /= 2;
	    while (localStack.isEmpty() == false)
		globalStack.push(localStack.pop());
	} // end while isRowEmpty is false
	System.out.println("......................................................");
    } // end displayTree()
//-------------------------------------------------------------
} // end class Tree
////////////////////////////////////////////////////////////////

class TreeApp {
    public static void main(String[] args) throws IOException {
	int value;
	Tree theTree = new Tree();

	theTree.insert(50, 1.5);
	theTree.insert(25, 1.2);
	theTree.insert(75, 1.7);
	theTree.insert(12, 1.5);
	theTree.insert(37, 1.2);
	theTree.insert(43, 1.7);
	theTree.insert(30, 1.5);
	theTree.insert(33, 1.2);
	theTree.insert(87, 1.7);
	theTree.insert(93, 1.5);
	theTree.insert(97, 1.5);

	while (true) {
	    System.out.print("Enter first letter of show, ");
	    System.out.print("insert, find, delete, or traverse: ");
	    int choice = getChar();
	    switch (choice) {
	    case 's':
		theTree.displayTree();
		break;
	    case 'i':
		System.out.print("Enter value to insert: ");
		value = getInt();
		theTree.insert(value, value + 0.9);
		break;
	    case 'f':
		System.out.print("Enter value to find: ");
		value = getInt();
		Node found = theTree.find(value);
		if (found != null) {
		    System.out.print("Found: ");
		    found.displayNode();
		    System.out.print("\n");
		} else
		    System.out.print("Could not find ");
		System.out.print(value + '\n');
		break;
	    case 'd':
		System.out.print("Enter value to delete: ");
		value = getInt();
		boolean didDelete = theTree.delete(value);
		if (didDelete)
		    System.out.print("Deleted " + value + '\n');
		else
		    System.out.print("Could not delete ");
		System.out.print(value + '\n');
		break;
	    case 't':
		System.out.print("Enter type 1, 2 or 3: ");
		value = getInt();
		theTree.traverse(value);
		break;
	    default:
		System.out.print("Invalid entry\n");
	    } // end switch
	} // end while
    } // end main()
//-------------------------------------------------------------

    public static String getString() throws IOException {
	InputStreamReader isr = new InputStreamReader(System.in);
	BufferedReader br = new BufferedReader(isr);
	String s = br.readLine();
	return s;
    }

//-------------------------------------------------------------
    public static char getChar() throws IOException {
	String s = getString();
	return s.charAt(0);
    }

//-------------------------------------------------------------
    public static int getInt() throws IOException {
	String s = getString();
	return Integer.parseInt(s);
    }
//-------------------------------------------------------------
}

class BagChooser {

    private int arr[];
    private int tWeight;
    private int length;
    private int nStart = 0;
    private List<Integer> resultList = new ArrayList<Integer>();
    private int recursionLevel = 0;

    public BagChooser(int[] res, int weight) {
	super();
	this.arr = res;
	this.tWeight = weight;
	this.length = arr.length - 1;
    }

    public void recursionChooser(int index) throws InterruptedException {
	finder(0, index);
    }

    private void finder(int weight, int index) {
	int start = arr[index];
	int check = start + weight;
	if (check == tWeight) {
	    resultList.add(start);
	    sL("Result:\n\t\tWeight " + tWeight + " found in mix: " + resultList.toString() + ".\n\t\tCheck sum: "
		    + resultList.stream().mapToInt(v -> v).sum() + ".\n\t\tRecursion level: " + recursionLevel
		    + ".\n\t\tLast array index: " + index + ".");
	    return;
	} else if (index >= length) {
	    if (nStart == length) {
		sL("Can't find weight: " + tWeight + ". Recursion level: " + recursionLevel + ".");
		return;
	    }
	    resultList.clear();
	    recursionLevel++;
	    sL("Index: " + index + ". Weignt: " + weight + ". nStart: " + nStart + ". Check array index: " + arr[index]
		    + ". Recursion level: " + recursionLevel + ".");
	    finder(0, ++nStart);
	} else if (check < tWeight) {
	    resultList.add(start);
	    recursionLevel++;
	    sL("Index: " + index + ". Weignt: " + weight + ". nStart: " + nStart + ". Check array index: " + arr[index]
		    + ". Recursion level: " + recursionLevel + ".");
	    finder(check, ++index);
	} else if (check > tWeight) {
	    recursionLevel++;
	    sL("Index: " + index + ". Weignt: " + weight + ". nStart: " + nStart + ". Check array index: " + arr[index]
		    + ". Recursion level: " + recursionLevel + ".");
	    finder(weight, ++index);
	}

    }

    private void sL(String msg) {
	System.err.print("Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber() + " --> ");
	System.out.println(msg);
    }

}

class RecursionTriangle {

    public static int recursionTriangle(int number) {
	StackTraceElement[] st = Thread.currentThread().getStackTrace();
	System.out.println();
	for (StackTraceElement o : st) {
	    System.out.println(o);
	}
	if (number == 1) {
	    return 1;
	}
	return number + recursionTriangle(number - 1);
    }

}

class BinarySearch {
    private int[] arr = { 0, 2, 4, 5, 6, 8, 12, 22, 65, 235, 633, 1111, 2112, 3313, 12314, 144125, 1124146, 1124127,
	    1124124458, 612341319 };

    public void search(int number) {
	int result = 0;
	int low = 0;
	int max = arr.length - 1;
	while (true) {
	    int index = (low + max) / 2;
	    if (arr[index] == number) {
		result = index;
		break;
	    } else if (low > max) {
		System.out.println("Can't find number: " + number);
		return;
	    } else {
		if (arr[index] < number) {
		    low = index + 1;
		} else {
		    max = index - 1;
		}
	    }
	}
	System.out.println("Found in position: " + (result + 1) + ", number is: " + arr[result]);
    }

    public void recursionSearch(int k, int l, int u) {
	StackTraceElement[] el = Thread.currentThread().getStackTrace();
	for (StackTraceElement st : el) {
	    System.out.println(st);
	}
	System.out.println();
	int cur = (l + u) / 2;
	if (arr[cur] == k) {
	    System.out.println("Found in position: " + (cur + 1) + ", number is: " + arr[cur]);
	    return;
	} else if (l > u) {
	    System.out.println("Can't find number: " + k);
	    return;
	} else {
	    if (arr[cur] < k) {
		recursionSearch(k, cur + 1, u);
	    } else {
		recursionSearch(k, l, cur - 1);
	    }
	}
    }

}

class Cherck {
    int i;
    String t;
    Object o;

    @Override
    public String toString() {
	return this.getClass().getSimpleName() + ":\n\t" + i + "\n\t" + t + "\n\t" + o.getClass().getSimpleName()
		+ "\n";
    }
}

@SuppressWarnings("all")
class ChanellTsest {
    private static final int bsize = 1024;
    StringBuilder sb = new StringBuilder();

    public ChanellTsest() throws Exception {

	/*
	 * BufferedReader br=new BufferedReader(new
	 * FileReader("C:\\Users\\"+System.getProperty("user.name")+"Desktop\\ee
	 * .txt")); while(br.ready()) { sb.append(br.readLine()); }
	 */

	FileChannel fc = new FileInputStream("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\dd.txt")
		.getChannel();
	ByteBuffer bf = ByteBuffer.allocate(bsize);
	fc.read(bf);
	bf.flip();
	while (bf.hasRemaining()) {
	    System.out.print((char) bf.get());
	}

	bf.rewind();

	String encoder = System.getProperty("file.encoding");
	sb.append("\n" + encoder + "\n" + Charset.forName(encoder).decode(bf));

	bf.rewind();

	fc = new RandomAccessFile("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\ee.txt", "rw")
		.getChannel();
	fc.position(fc.size() / 2);
	fc.write(ByteBuffer.wrap(sb.toString().getBytes()));

	/*
	 * DataOutputStream dos=new DataOutputStream(new
	 * FileOutputStream("C:\\Users\\"+System.getProperty("user.name")+"\\
	 * Desktop\\dd.txt")); dos.write(sb.toString().getBytes()); dos.close();
	 */
	fc.close();
    }

}

class ThreadA extends Thread {

    static volatile int q = 0;
    static final Object o = new Object();

    @Override
    public void run() {
	for (int i = 0; i < 1_000_000; i++) {
	    synchronized (o) {
		q++;
	    }
	}
    }
}

class TestThreadA {
    public TestThreadA() throws Exception {
	System.out.println("Start multithread part of test...");
	long result = 0;
	long time = System.nanoTime();
	for (int i = 0; i < 10; i++) {
	    new ThreadA().start();
	}
	Thread.sleep(4000);
	System.out.println(
		"q = " + ThreadA.q + ", time = " + (((double) (System.nanoTime() - time) / 1_000_000_000) - 4d));
	System.out.println("Start singlethread part of test...");
	long time2 = System.nanoTime();
	for (int i = 0; i < ThreadA.q; i++) {
	    result++;
	}
	System.out.println("r = " + result + ", time = " + (double) (System.nanoTime() - time2) / 1_000_000_000);
    }
}

enum Podor {
    HEHE, HAHA;

    public String toString() {
	String id = name();
	String low = id.substring(1).toLowerCase();
	return id.charAt(0) + low;
    }
}

interface A1 {
}

interface A2 {
}

@SuppressWarnings("all")
class A3 implements A1, A2 {
    public <A1> void meth1() {
	System.out.println("meth1");
    }

    public <A2> void meth2() {
	System.out.println("meth2");
    }

    public <T> void meth5(T t) {
	System.out.println(t.getClass().getName());
    }

}

interface CheChe {
    Che create(int q);

}

class Che {
    private int q;

    Che(int q) {
	this.q = q;
    }

    void show() {
	System.out.println(q);
    }
}

@SuppressWarnings("all")
class Qf<T> {
    private T[] arr;

    public Qf(int s) {
	arr = (T[]) new Object[s];
    }

    public T get(int i) {
	return arr[i];
    }

    public void add(int s, T t) {
	arr[s] = t;
    }

    public T[] arrget() {
	return arr;
    }
}

@SuppressWarnings("all")
class Take extends JPanel {

    private static final int SCALEFACTOR = 200;
    private int cycles, points;
    private double[] sines;
    private int[] pts;

    public Take() {
	setCycles(5);
    }

    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	int maxWidth = getWidth();
	int maxHeight = getHeight();
	double hstep = (double) maxWidth / (double) points;
	pts = new int[points];
	for (int i = 0; i < points; i++) {
	    pts[i] = (int) (sines[i] * maxHeight / 2 * .95 + maxWidth / 2);
	}
	g.setColor(Color.RED);
	for (int i = 1; i < points; i++) {
	    int x1 = (int) ((i - 1) * hstep);
	    int x2 = (int) (i * hstep);
	    int y1 = pts[i - 1];
	    int y2 = pts[i];
	    g.drawLine(x1, y1, x2, y2);
	}

    }

    void setCycles(int newCycles) {
	cycles = newCycles;
	points = SCALEFACTOR * cycles * 2;
	sines = new double[points];
	for (int i = 0; i < points; i++) {
	    double rad = (Math.PI / SCALEFACTOR) * i;
	    sines[i] = Math.sin(rad);
	}
	repaint();
    }

}

@SuppressWarnings("all")
class Add extends JFrame {
    JColorChooser j = new JColorChooser();
    JProgressBar sb = new JProgressBar();
    ProgressMonitor pm;
    JButton b = new JButton();
    JLabel lab = new JLabel("HAHA", JLabel.CENTER);
    JButton exit = new JButton("<html><b><i><font size=-2>Exit baton</i></b></html>");
    final Object o = new Object();
    JSlider js = new JSlider();
    volatile boolean x = false;
    int state = 0;
    Color sqe;
    {
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {
	}
    }

    public Add() {
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setSize(500, 200);
	setLocationRelativeTo(null);
	setLayout(new GridLayout(4, 1));
	b.setText("Baton obichnii");
	b.setSize(100, 50);
	sb.setStringPainted(true);
	exit.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
	    }
	});

	b.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		Container cc = getContentPane();
		sqe = JColorChooser.showDialog(cc, "1", lab.getBackground());
		x = true;
		// setVisible(false);
		progress();
		change();
	    }
	});
	b.setFocusable(false);
	exit.setFocusable(false);
	add(sb);
	add(exit);
	add(b);
	add(lab);
	/*
	 * try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
	 * catch (Exception e) {}
	 */
	setVisible(true);

    }

    void progress() {
	if (x) {
	    new Thread(() -> {
		pm = new ProgressMonitor(this, "Monotor", "test", 0, 100);
		synchronized (o) {
		    for (int i = 0; i < 101; i++) {
			if (pm.isCanceled()) {
			    o.notifyAll();
			    JOptionPane.showMessageDialog(this, "Operation been canceled", "Error",
				    JOptionPane.ERROR_MESSAGE);
			    break;
			}
			Color c = new Color(i);
			pm.setProgress(i);
			sb.setValue(i);
			sb.setForeground(c);
			try {
			    Thread.sleep((int) (Math.random() * 100));
			} catch (InterruptedException esa) {
			}
		    }
		    x = false;
		    o.notifyAll();
		}
		// setVisible(true);
	    }).start();
	}
    }

    void change() {
	new Thread(() -> {
	    synchronized (o) {
		while (x) {
		    try {
			o.wait();
		    } catch (Exception e1) {

		    }
		}
		if (!pm.isCanceled()) {
		    lab.setForeground(sqe);
		    exit.setBackground(sqe);
		    lab.setText(String.format("<html><font size=+2>%s", sqe.toString()));
		    try {
			Thread.sleep(3000);
		    } catch (InterruptedException e) {

		    }
		    lab.setText(Double.toString(Math.random()));
		}
	    }
	}).start();
    }

}

class DD {
    public <T> void viod(T x, T y, T z) {
	System.out.println(x.getClass().getName() + " " + y.getClass().getName() + " " + z.getClass().getName());
    }
}

class Rome {

    /*
     * public static void main(String[] args) throws IOException { BufferedReader
     * bufferedReader = new BufferedReader(new InputStreamReader(System.in));
     * System.out.println("Input a roman number to be converted to decimal: ");
     * String romanString = bufferedReader.readLine();
     * System.out.println("Conversion result equals " +
     * romanToInteger(romanString)); }
     */

    public static int romanToInteger(String s) {
	int result = 0;
	s = s.trim().toUpperCase();
	String[] temp = s.split("");
	for (int i = 0; i < temp.length; i++) {
	    // System.err.println(i);
	    String c = temp[i];
	    if (i < temp.length - 1) {
		if (c.equals("I") && temp[i + 1].equals("V")) {
		    c = "IV";
		    i++;// 4
		}

		if (c.equals("I") && temp[i + 1].equals("X")) {
		    c = "IX";
		    i++;// 9
		}
		if (c.equals("X") && temp[i + 1].equals("L")) {
		    c = "XL";
		    i++;// 40
		}
		if (c.equals("X") && temp[i + 1].equals("C")) {
		    c = "XC";
		    i++;// 90
		}
		if (c.equals("C") && temp[i + 1].equals("D")) {
		    c = "CD";
		    i++;// 400
		}
		if (c.equals("C") && temp[i + 1].equals("M")) {
		    c = "CM";
		    i++;// 900
		}
	    }

	    switch (c) {
	    case "I": {
		result += 1;
		break;
	    }
	    case "IV": {
		result += 4;
		break;
	    }
	    case "V": {
		result += 5;
		break;
	    }
	    case "IX": {
		result += 9;
		break;
	    }
	    case "X": {
		result += 10;
		break;
	    }
	    case "XL": {
		result += 40;
		break;
	    }
	    case "L": {
		result += 50;
		break;
	    }
	    case "XC": {
		result += 90;
		break;
	    }
	    case "C": {
		result += 100;
		break;
	    }
	    case "CD": {
		result += 400;
		break;
	    }
	    case "D": {
		result += 500;
		break;
	    }
	    case "CM": {
		result += 900;
		break;
	    }
	    case "M": {
		result += 1000;
		break;
	    }
	    default:
		throw new IllegalArgumentException("Unexpected value: " + c);
	    }
	}
	return result;
    }
}
