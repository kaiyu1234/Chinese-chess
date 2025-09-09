import io.binghe.chess.piece.impl.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.swing.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JOptionPane.class})
public class ChessPieceTest {

    // 棋盘初始化（10行9列标准象棋棋盘）
    private int[][] emptyBoard;
    private int[][] boardWithPieces;

    @Before
    public void setUp() {
        // 空棋盘
        emptyBoard = new int[10][9];

        // 带初始棋子的棋盘
        boardWithPieces = new int[10][9];
        boardWithPieces[0][4] = 'G';       // 黑将
        boardWithPieces[9][4] = 1000 + 'G';// 红帅
        boardWithPieces[0][0] = 'C';       // 黑车
        boardWithPieces[9][8] = 1000 + 'C';// 红车
        boardWithPieces[2][1] = 'H';       // 黑马
        boardWithPieces[7][7] = 1000 + 'H';// 红马
    }

    // 测试将/帅
    @Test
    public void testGeneralChess() {
        GeneralChess general = new GeneralChess();

        // 黑将合法移动（九宫格内上下左右一格）
        assertTrue(general.check(emptyBoard, 0, 4, 0, 3, true));
        assertTrue(general.check(emptyBoard, 0, 4, 1, 4, true));

        // 黑将非法移动（九宫格外）
        assertFalse(general.check(emptyBoard, 0, 4, 0, 2, true));
        assertFalse(general.check(emptyBoard, 0, 4, 3, 4, true));

        // 红帅合法移动
        assertTrue(general.check(emptyBoard, 9, 4, 9, 5, false));
        assertTrue(general.check(emptyBoard, 9, 4, 8, 4, false));

        // 红帅非法移动
        assertFalse(general.check(emptyBoard, 9, 4, 9, 2, false));
        assertFalse(general.check(emptyBoard, 9, 4, 6, 4, false));

        // 将帅对面（无阻挡）- 黑方胜利
        mockStatic(JOptionPane.class);
        assertTrue(general.check(emptyBoard, 0, 4, 9, 4, true));

        // 将帅对面（有阻挡）- 不能胜利
        int[][] blockedBoard = new int[10][9];
        blockedBoard[5][4] = 'C'; // 中间有棋子
        assertFalse(general.check(blockedBoard, 0, 4, 9, 4, true));
    }

    // 测试车
    @Test
    public void testCarChess() {
        CarChess car = new CarChess();

        // 横向合法移动（无阻挡）
        assertTrue(car.check(emptyBoard, 0, 0, 0, 5, true));

        // 纵向合法移动（无阻挡）
        assertTrue(car.check(emptyBoard, 0, 0, 5, 0, true));

        // 斜向移动（非法）
        assertFalse(car.check(emptyBoard, 0, 0, 1, 1, true));

        // 有阻挡（非法）
        int[][] blockedBoard = new int[10][9];
        blockedBoard[0][3] = 'H'; // 中间有棋子
        assertFalse(car.check(blockedBoard, 0, 0, 0, 5, true));

        // 吃对方将（胜利）
        mockStatic(JOptionPane.class);
        assertTrue(car.check(emptyBoard, 0, 0, 9, 4, true));
    }

    // 测试马
    @Test
    public void testHorseChess() {
        HorseChess horse = new HorseChess();

        // 马走日（合法）
        assertTrue(horse.check(emptyBoard, 2, 1, 3, 3, true));
        assertTrue(horse.check(emptyBoard, 2, 1, 4, 2, true));

        // 不是日字（非法）
        assertFalse(horse.check(emptyBoard, 2, 1, 2, 3, true));

        // 马脚被绊（非法）
        int[][] blockedBoard = new int[10][9];
        blockedBoard[2][2] = 'C'; // 绊马脚
        assertFalse(horse.check(blockedBoard, 2, 1, 3, 3, true));

        // 吃对方帅（胜利）
        mockStatic(JOptionPane.class);
        assertTrue(horse.check(emptyBoard, 7, 7, 9, 4, false));
    }

    // 测试炮
    @Test
    public void testCannonChess() {
        CannonChess cannon = new CannonChess();

        // 空走（无棋子无炮架）
        assertTrue(cannon.check(emptyBoard, 0, 2, 0, 5, true));

        // 有炮架吃子
        int[][] boardWithPlatform = new int[10][9];
        boardWithPlatform[0][4] = 'H'; // 炮架
        boardWithPlatform[0][6] = 1000 + 'S'; // 对方棋子
        assertTrue(cannon.check(boardWithPlatform, 0, 2, 0, 6, true));

        // 无炮架吃子（非法）
        int[][] noPlatformBoard = new int[10][9];
        noPlatformBoard[0][6] = 1000 + 'S';
        assertFalse(cannon.check(noPlatformBoard, 0, 2, 0, 6, true));

        // 两个炮架（非法）
        int[][] twoPlatformsBoard = new int[10][9];
        twoPlatformsBoard[0][3] = 'H';
        twoPlatformsBoard[0][4] = 'C';
        twoPlatformsBoard[0][6] = 1000 + 'S';
        assertFalse(cannon.check(twoPlatformsBoard, 0, 2, 0, 6, true));

        // 打帅（胜利）
        mockStatic(JOptionPane.class);
        int[][] cannonCheckBoard = new int[10][9];
        cannonCheckBoard[5][4] = 'C'; // 炮架
        assertTrue(cannon.check(cannonCheckBoard, 0, 4, 9, 4, true));
    }

    // 测试象
    @Test
    public void testElephantChess() {
        ElephantChess elephant = new ElephantChess();

        // 黑象合法移动（田字，不过河）
        assertTrue(elephant.check(emptyBoard, 0, 2, 2, 4, true));

        // 黑象过河（非法）
        assertFalse(elephant.check(emptyBoard, 0, 2, 5, 5, true));

        // 红象合法移动
        assertTrue(elephant.check(emptyBoard, 9, 2, 7, 4, false));

        // 红象过河（非法）
        assertFalse(elephant.check(emptyBoard, 9, 2, 4, 5, false));

        // 田心有棋子（绊象腿）
        int[][] blockedBoard = new int[10][9];
        blockedBoard[1][3] = 'C';
        assertFalse(elephant.check(blockedBoard, 0, 2, 2, 4, true));
    }

    // 测试士
    @Test
    public void testChapChess() {
        ChapChess chap = new ChapChess();

        // 黑士合法移动（九宫格内斜线）
        assertTrue(chap.check(emptyBoard, 0, 4, 1, 3, true));
        assertTrue(chap.check(emptyBoard, 0, 4, 1, 5, true));

        // 黑士出九宫（非法）
        assertFalse(chap.check(emptyBoard, 0, 4, 2, 6, true));

        // 红士合法移动
        assertTrue(chap.check(emptyBoard, 9, 4, 8, 3, false));

        // 红士走直线（非法）
        assertFalse(chap.check(emptyBoard, 9, 4, 9, 5, false));

        // 红士出九宫（非法）
        assertFalse(chap.check(emptyBoard, 9, 4, 6, 4, false));
    }
}