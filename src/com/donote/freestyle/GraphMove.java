package com.donote.freestyle;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GraphMove extends View {

	// 简单识别
	// 如果有一段弧线则按圆处理，其余全部是矩形处理

	DefinePoint Center = new DefinePoint(0, 0);

	public float width = 0;
	public float height = 0;
	public float radius = 0;

	private boolean isTable = false;
	private boolean isCircle = false;
	private boolean isRect = false;
	int rows = 0;
	int columns = 0;

	// 获取部分点的坐标
	List<DefinePoint> points;
	List<List<DefinePoint>> lists = new ArrayList<List<DefinePoint>>();

	int flag = 0;
	float cur_x = 0, cur_y = 0;
	Canvas m_canvas = null;
	Paint m_paint = new Paint();
	Path m_path = new Path();

	public GraphMove(Context context) {
		super(context);

		m_canvas = new Canvas();
		m_paint.setAntiAlias(true);
		m_paint.setDither(true);
		m_paint.setColor(Color.RED);
		m_paint.setStrokeWidth(5);
		m_paint.setStyle(Paint.Style.STROKE);
		m_paint.setStrokeJoin(Paint.Join.ROUND);
		m_paint.setStrokeCap(Paint.Cap.ROUND);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		if (m_path != null) {
			canvas.drawPath(m_path, m_paint);
		}
	}

	public void touchdown(float x, float y) {
		m_path.moveTo(x, y);
		cur_x = x;
		cur_y = y;
	}

	public void touchmove(float x, float y) {
		float dx = Math.abs(x - cur_x);
		float dy = Math.abs(y - cur_y);

		if (dx >= 4 || dy >= 4) {
			m_path.quadTo(cur_x, cur_y, (x + cur_x) / 2, (y + cur_y) / 2);
			cur_x = x;
			cur_y = y;
		}
	}

	public void touchup(float x, float y) {
		m_path.lineTo(x, y);
		m_canvas.drawPath(m_path, m_paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			touchdown(x, y);
			invalidate();

			points = new ArrayList<DefinePoint>();
			break;
		case MotionEvent.ACTION_MOVE:

			DefinePoint point = new DefinePoint(x, y);
			points.add(point);

			touchmove(event.getX(), event.getY());
			invalidate();
			break;

		case MotionEvent.ACTION_UP:

			lists.add(points);

			touchup(event.getX(), event.getY());
			invalidate();

			break;
		}
		return true;
	}

	public void Definefigure() {

		// 获得中心点坐标

		if(lists.size() == 0){
			return;
		}
		
		float center_x = 0;
		float center_y = 0;

		int count = 0;
		for (int i = 0; i < lists.size(); i++) {
			for (int j = 0; j < lists.get(i).size(); j++) {
				DefinePoint point = lists.get(i).get(j);
				center_x += point.getX();
				center_y += point.getY();
				count++;
			}
		}

		DefinePoint minPoint = new DefinePoint(7000, 7000);
		DefinePoint maxPoint = new DefinePoint(0, 0);

		center_x = center_x / points.size();
		center_y = center_y / points.size();

		float alldis = 0;

		for (int i = 0; i < lists.size(); i++) {
			for (int j = 0; j < lists.get(i).size(); j++) {
				// 获取最小点
				DefinePoint point = lists.get(i).get(j);
				if ((minPoint.getX() + minPoint.getY()) > (point.getX() + point
						.getY())) {
					minPoint = point;
				}
				// 获取最大点
				if ((maxPoint.getX() + maxPoint.getY()) < (point.getX() + point
						.getY())) {
					maxPoint = point;
				}

				float distance = (float) Math.sqrt((point.getX() - center_x)
						* (point.getX() - center_x) + (point.getY() - center_y)
						* (point.getY() - center_y));

				alldis = alldis + distance;
				point.setDis(distance);

				lists.get(i).set(j, point);
			}
		}

		width = maxPoint.getX() - minPoint.getX();
		height = maxPoint.getY() - minPoint.getY();

		float average = alldis / count;

		float limit = 0;
		for (int i = 0; i < lists.size(); i++) {
			for (int j = 0; j < lists.get(i).size(); j++) {
				DefinePoint point = lists.get(i).get(j);
				limit = limit + (average - point.getDis())
						* (average - point.getDis());
			}
		}
		limit = (float) Math.sqrt(limit) / count;
		
	

		if (limit < 4) {

			Center.setX(center_x);
			Center.setY(center_y);
			radius = average;

			isCircle = true;
			isRect = false;
			isTable = false;

		} else if (4 < limit && limit < 7) {

			Center.setDis(0);
			if (width > height) {
				height = width;
			} else {
				width = height;
			}
			isRect = true;
			isCircle = false;
			isTable = false;
			
		} else if (7 < limit ) {
			Center.setDis(0);
			isRect = true;
			isCircle = false;
			isTable = false;
		}

		float offset = 29;
		boolean isrow = true;
		boolean iscolumn = true;
		float start_x;
		float start_y;
		
		for (int i = 0; i < lists.size(); i++) {
			start_x = lists.get(i).get(0).getX();
			start_y = lists.get(i).get(0).getY();
			
			for (int j = 1; j < lists.get(i).size() && iscolumn == true; j++) {
				if ((Math.abs(start_x - lists.get(i).get(j).getX())) < offset) {
					iscolumn = true;
				} else {
					iscolumn = false;
				}
			}

			if (iscolumn == true) {
				columns++;
			} else {
				iscolumn = true;
			}

			for (int j = 1; j < lists.get(i).size() && isrow == true; j++) {
				if ((Math.abs(start_y - lists.get(i).get(j).getY())) < offset) {
					isrow = true;
				} else {
					isrow = false;
				}
			}

			if (isrow == true) {
				rows++;
			} else {
				isrow = true;
			}
		
		}

		if (columns >= 2 && rows >=2 && (columns +rows)>4) {
			
			Log.i("table2", String.valueOf(rows) + ":"
					+ String.valueOf(columns));
			
			isTable = true;
			isCircle = false;
			isRect = false;
		}

	}

	/**
	 * @return the isTable
	 */
	public boolean isTable() {
		return isTable;
	}

	/**
	 * @param isTable the isTable to set
	 */
	public void setTable(boolean isTable) {
		this.isTable = isTable;
	}

	/**
	 * @return the isCircle
	 */
	public boolean isCircle() {
		return isCircle;
	}

	/**
	 * @param isCircle the isCircle to set
	 */
	public void setCircle(boolean isCircle) {
		this.isCircle = isCircle;
	}

	/**
	 * @return the isRect
	 */
	public boolean isRect() {
		return isRect;
	}

	/**d./
	 * @param isRect the isRect to set
	 */
	public void setRect(boolean isRect) {
		this.isRect = isRect;
	}

	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows-1;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * @return the columns
	 */
	public int getColumns() {
		return columns-1;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	

}
