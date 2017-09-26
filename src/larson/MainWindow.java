/******************************************************************
 *
 *    Java Lib For Android, Powered By S先生.
 *
 *    Copyright (c) 2017-2017 Digital Telemedia Co.,Ltd
 *    http://www.d-telemedia.com/
 *
 *    Package:     larson
 *
 *    Filename:    MainWindow.java
 *
 *    Description: TODO(用来实现快速翻译)
 *
 *    Copyright:   Copyright (c) 2001-2014
 *
 *    Company:     Digital Telemedia Co.,Ltd
 *
 *    @author:     S先生
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年9月25日 上午10:20:57
 *
 *    Revision:
 *
 *    2017年9月25日 上午10:20:57
 *        - first revision
 *
 *****************************************************************/
package larson;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JFrame;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;

/**
 * @ClassName MainWindow
 * @Description TODO(快译的电脑版)
 * @author S先生
 * @Date 2017年9月25日 上午10:20:57
 * @version 1.0.0
 */
public class MainWindow implements DocumentListener,TextListener,WindowFocusListener{

	private JFrame frame;
	
	private TextArea edt_fanyi_out;
	private TextField edt_fanyi_input;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}
	
	/*
	 * 
	 *初始化布局
	 */

	public void initLayout(){
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 279);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("XP快译");
		frame.getContentPane().setLayout(null);
		//frame.setAlwaysOnTop(true);//设置总是在顶部
		
		Label label = new Label("输入翻译字符：");
		label.setBounds(10, 10, 85, 23);
		frame.getContentPane().add(label);
		
		edt_fanyi_out = new TextArea();
		edt_fanyi_out.setBounds(10, 46, 414, 165);
		frame.getContentPane().add(edt_fanyi_out);
		
		Label label_1 = new Label("XP快译的电脑版，By S先生");
		label_1.setAlignment(Label.RIGHT);
		label_1.setBounds(10, 217, 414, 23);
		frame.getContentPane().add(label_1);
		
		edt_fanyi_input = new TextField();
		edt_fanyi_input.setBounds(101, 10, 323, 23);
		frame.getContentPane().add(edt_fanyi_input);
	}
	
	/*
	 * 
	 * 初始化监听
	 */
	
	public void initListener(){
		
		frame.addWindowFocusListener(this);

		edt_fanyi_input.addTextListener(this);
		
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		initLayout();
		
		initListener();

	}
	/** 
     * 从剪切板获得文字。 
     */  
    public static String getSysClipboardText() {  
        String ret = "";  
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();  
        // 获取剪切板中的内容  
        Transferable clipTf = sysClip.getContents(null);  
  
        if (clipTf != null) {  
            // 检查内容是否是文本类型  
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {  
                try {  
                    ret = (String) clipTf  
                            .getTransferData(DataFlavor.stringFlavor);  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
  
        return ret;  
    } 
    
    /*
     * 
     * 整理字符
     * 
     */
    public static String sortTrans(String transStr){
    	
    	if("".equals(transStr)|null==transStr)return "";
    	
    	return transStr.replaceAll("(?<=[a-zA-Z])(?=[A-Z][a-z])", " ")
    			.replaceAll("_", " ")
    			.replaceAll("[(){}\\[\\]]","");
    	
    	
    }
    /*
     * 
     * 获取google翻译结果
     * 
     * 
     */
    
	public String getGoogleFanyiUrl(String toLanguage, String q)
	{
		// TODO: Implement this method
		try {
			return "http://ainixiang.cn/fanyi/?sl=auto&tl=" +toLanguage + "&q=" + URLEncoder.encode(q,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return "";
		
	}
	
	/*
	 * 
	 * 实现翻译
	 * 
	 */

	static boolean isTransing=false;
	
	public void toTrans(String strTrans){
		
		if(isTransing)return;
		
		isTransing=true;
		
		HttpUtils.doGetAsyn(getGoogleFanyiUrl(LanguageUtils.ischina(strTrans)?"en":"zh-CN",sortTrans(strTrans)) , new HttpUtils.CallBack() {
			
			@Override
			public void onRequestComplete(int what, String result) {
				// TODO 自动生成的方法存根
				
				if(what==0){
					
					edt_fanyi_out.setText("出现错误："+result);
					
					return;
					
				}
				
				JSONObject jsonO=JSONObject.fromObject(result);
				
				result=JSONArray.fromObject(jsonO.getString("tran")).getString(0);
	
				edt_fanyi_out.setText(result);
				
				isTransing=false;
				
			}
		});
		
	}

	/* (非 Javadoc)
	 * Description:
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		// TODO 自动生成的方法存根
		toTrans(edt_fanyi_input.getText());
	}

	/* (非 Javadoc)
	 * Description:
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO 自动生成的方法存根
		toTrans(edt_fanyi_input.getText());
	}

	/* (非 Javadoc)
	 * Description:
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO 自动生成的方法存根
		
	}

	/* (非 Javadoc)
	 * Description:
	 * @see java.awt.event.TextListener#textValueChanged(java.awt.event.TextEvent)
	 */

	@Override
	public void textValueChanged(TextEvent e) {
	
		toTrans(edt_fanyi_input.getText());

	}

	/* (非 Javadoc)
	 * Description:
	 * @see java.awt.event.WindowFocusListener#windowGainedFocus(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowGainedFocus(WindowEvent e) {
		// TODO 自动生成的方法存根
		 String strClipdata=getSysClipboardText();

			edt_fanyi_input.setText(strClipdata);
			
			toTrans(strClipdata);
	}

	/* (非 Javadoc)
	 * Description:
	 * @see java.awt.event.WindowFocusListener#windowLostFocus(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowLostFocus(WindowEvent e) {
		// TODO 自动生成的方法存根
		
	}

}
