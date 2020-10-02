/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.ui;

import com.intellij.util.ui.DrawUtil;
import com.intellij.util.ui.UIUtil;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

/**
 * @author Konstantin Bulenkov
 */
@SuppressWarnings("UseJBColor")
public class JBColor extends Color {

    private static volatile boolean DARK = DrawUtil.isUnderDarcula();

    private final Color darkColor;



    public JBColor(Color regular, Color dark) {
        super(regular.getRGB(), regular.getAlpha() != 255);
        darkColor = dark;
        //noinspection AssignmentToStaticFieldFromInstanceMethod
        DARK = DrawUtil.isUnderDarcula(); //Double check. Sometimes DARK != isDarcula() after dialogs appear on splash screen

    }

    public static boolean isBright() {
        return !DARK;
    }

    Color getDarkVariant() {
        return darkColor;
    }

    Color getColor() {
        return DARK ? getDarkVariant() : this;

    }

    @Override
    public int getRed() {
        final Color c = getColor();
        return c == this ? super.getRed() : c.getRed();
    }

    @Override
    public int getGreen() {
        final Color c = getColor();
        return c == this ? super.getGreen() : c.getGreen();
    }

    @Override
    public int getBlue() {
        final Color c = getColor();
        return c == this ? super.getBlue() : c.getBlue();
    }

    @Override
    public int getAlpha() {
        final Color c = getColor();
        return c == this ? super.getAlpha() : c.getAlpha();
    }

    @Override
    public int getRGB() {
        final Color c = getColor();
        return c == this ? super.getRGB() : c.getRGB();
    }


    @Override
    public int hashCode() {
        final Color c = getColor();
        return c == this ? super.hashCode() : c.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        final Color c = getColor();
        return c == this ? super.equals(obj) : c.equals(obj);
    }

    @Override
    public String toString() {
        final Color c = getColor();
        return c == this ? super.toString() : c.toString();
    }

    @Override
    public float[] getRGBComponents(float[] compArray) {
        final Color c = getColor();
        return c == this ? super.getRGBComponents(compArray) : c.getRGBComponents(compArray);
    }

    @Override
    public float[] getRGBColorComponents(float[] compArray) {
        final Color c = getColor();
        return c == this ? super.getRGBComponents(compArray) : c.getRGBColorComponents(compArray);
    }

    @Override
    public float[] getComponents(float[] compArray) {
        final Color c = getColor();
        return c == this ? super.getComponents(compArray) : c.getComponents(compArray);
    }

    @Override
    public float[] getColorComponents(float[] compArray) {
        final Color c = getColor();
        return c == this ? super.getColorComponents(compArray) : c.getColorComponents(compArray);
    }

    @Override
    public float[] getComponents(ColorSpace cspace, float[] compArray) {
        final Color c = getColor();
        return c == this ? super.getComponents(cspace, compArray) : c.getComponents(cspace, compArray);
    }

    @Override
    public float[] getColorComponents(ColorSpace cspace, float[] compArray) {
        final Color c = getColor();
        return c == this ? super.getColorComponents(cspace, compArray) : c.getColorComponents(cspace, compArray);
    }

    @Override
    public ColorSpace getColorSpace() {
        final Color c = getColor();
        return c == this ? super.getColorSpace() : c.getColorSpace();
    }

    @Override
    public synchronized PaintContext createContext(ColorModel cm, Rectangle r, Rectangle2D r2d, AffineTransform xform, RenderingHints hints) {
        final Color c = getColor();
        return c == this ? super.createContext(cm, r, r2d, xform, hints) : c.createContext(cm, r, r2d, xform, hints);
    }

    @Override
    public int getTransparency() {
        final Color c = getColor();
        return c == this ? super.getTransparency() : c.getTransparency();
    }

    public static final JBColor white = new JBColor(Color.white, UIUtil.getListBackground()) {
        @Override
        Color getDarkVariant() {
            return UIUtil.getListBackground();
        }
    };

    public static final JBColor black = new JBColor(Color.black, foreground());

    public static final JBColor green = new JBColor(Color.green, new Color(98, 150, 85));



    public static Color foreground() {
        return null;
    }

    public static Color background() {
        return null;
    }

}
