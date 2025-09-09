/**
 * Copyright 2020-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.binghe.chess.canvas;

import io.binghe.chess.check.RangeChecker;
import io.binghe.chess.manager.ChessManager;
import io.binghe.chess.piece.Chess;
import io.binghe.chess.utils.ImageTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author binghe
 * @version 1.0.0
 * @description 画图类
 */
public class Canvas extends JPanel {

    public static final int ROW_COUNT = 10;//棋盘行数
    public static final int COLUMN_COUNT = 9;//棋盘列数
    private int[][] map = new int[ROW_COUNT][COLUMN_COUNT];
    private Font font;
    public static final int BLACK = 1000;
    public static final int WHITE = 0;

    private boolean isBlack = false; //当前是谁下子
    private int selectColumn = -1;
    private int selectRow = -1;
    private ChessManager manager = new ChessManager();
    private Image mainGif;


    private boolean isValidPosition(int r, int c) {
        return r >= 0 && r < ROW_COUNT && c >= 0 && c < COLUMN_COUNT;
    }
    private boolean hasPiece(int r, int c) {
        // 先校验坐标有效性再检查棋子
        return isValidPosition(r, c) && map[r][c] != 0;
    }

    public Canvas(){//棋盘为10行9列，默认就好，不用改
        font = new Font("宋体",Font.BOLD,30);
        initMap();

        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                //super.mouseClicked(e);
                //System.out.println(e.getButton());
                if(e.getButton()==MouseEvent.BUTTON1){
                    int x = e.getX();
                    int y = e.getY();
                    //System.out.println(e.getX()+","+e.getY()+"["+x+","+y+"]");
                    int r = (y - 25) /58;
                    int c = (x - 25) /58;
                    //System.out.println("r = "+r+", c = "+c);
                    //选子
                    /*if(selectColumn == -1&&map[r][c]!=0){
                        if(RangeChecker.selectInRange(map,r,c,isBlack)){//isBlack 是 红方
                            selectColumn = c;
                            selectRow = r;
                            repaint();
                        }
                        else{
                            showFail("请选择己方棋子");
                        }
                        return ;// 选子阶段结束
                    }*/
                    if(selectColumn == -1) {
                        // 🔑 关键修改：使用验证函数
                        if(hasPiece(r, c)) {
                            // 原有选子逻辑
                            if(RangeChecker.selectInRange(map, r, c, isBlack)){
                                selectColumn = c;
                                selectRow = r;
                                repaint();
                            } else {
                                showFail("请选择己方棋子");
                            }
                        } else {
                            showFail("该位置无棋子"); // 明确提示
                        }
                        return; // 结束选子阶段
                    }

                    boolean valid  = RangeChecker.inRange(map, selectRow, selectColumn,r, c, isBlack);
                    if(!valid){
                        showFail("下子成功");
                    }

                    Chess chess = manager.getChess(map[selectRow][selectColumn]);
                    valid = chess.check(map, selectRow, selectColumn, r, c, isBlack);
                    if(!valid||(map[selectRow][selectColumn]<1000&&map[r][c]<1000&&map[r][c]!=0)||(map[selectRow][selectColumn]>1000&&map[r][c]>1000&&map[r][c]!=0)){
                        showFail("下子失败,规则不允许");
                        selectColumn = -1;
                        selectRow = -1;
                        repaint();
                        return ;
                    }

                    map[r][c] = map[selectRow][selectColumn];
                    map[selectRow][selectColumn] = 0;
                    selectColumn = -1;
                    selectRow = -1;
                    isBlack = !isBlack;//对方来下
                    repaint();
                }
            }
        });
    }
    protected void showFail(String msg) {
        System.out.println(msg);
    }

    public void initMap(){
        //初始化棋盘
        map=new int [][]{{1000+'C',1000+'H',1000+'E',1000+'S',1000+'G',1000+'S',1000+'E',1000+'H',1000+'C'}, {0,0,0,0,0,0,0,0,0,},
                {0,1000+'P',0,0,0,0,0,1000+'P',0},{1000+'A',0,1000+'A',0,1000+'A',0,1000+'A',0,1000+'A'},
                {0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0},
                {'A',0,'A',0,'A',0,'A',0,'A'},{0,'P',0,0,0,0,0,'P',0},
                {0,0,0,0,0,0,0,0,0},{'C','H','E','S','G','S','E','H','C'}};
        mainGif = ImageTools.loadImage("main.gif");
        manager.loadAllImage();
    }

    //内部类
    public static class MyPoint{
        public int r;
        public int c;
        public MyPoint(int r,int c) {
            this.r = r;
            this.c = c;
        }
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        g.setFont(font);
        int w = this.getWidth();
        int h = this.getHeight();
        g.drawImage(mainGif, 0, 0,null);

        for(int r = 0;r<map.length;r++){
            for(int c = 0;c<map[r].length;c++){
                safelyDraw(g, manager.getImage(map[r][c]), 25+58*c, r*58+25);
            }
        }
        g.drawRect(25+58* selectColumn, 25+58* selectRow, 58, 58);
        g.drawString(isBlack?"黑方下棋":"红方下棋", 220 , 650);

    }

    public void safelyDraw(Graphics g,Image img,int x,int y){
        try {
            g.drawImage(img, x, y,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }

    public void setSelectColumn(int selectColumn) {
        this.selectColumn = selectColumn;
    }

    public void setSelectRow(int selectRow) {
        this.selectRow = selectRow;
    }
}
