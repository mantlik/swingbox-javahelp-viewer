/*
 * ContentImage.java
 * Copyright (c) 2005-2012 Radek Burget
 *
 * CSSBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * CSSBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with CSSBox. If not, see <http://www.gnu.org/licenses/>.
 *
 * Created on 12.6.2012, 9:30:12 by burgetr
 */
package org.fit.cssbox.layout;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.URL;

/**
 * Generic image used in the page content (used by ReplacedImage and BackgroundImage).
 * 
 * @author burgetr
 */
public abstract class ContentImage extends ReplacedContent implements ImageObserver
{
    /** Used when there are no image data */
    protected final int DEFAULT_IMAGE_WIDTH = 0;
    /** Used when there are no image data */
    protected final int DEFAULT_IMAGE_HEIGHT = 0;
    /** Image loading timeout [ms] */
    protected final int LOAD_TIMEOUT = 500;
    
    protected boolean loadImages; //is the image loading switched on?
    protected boolean caching; //use picture caching?
    protected URL url; //image url
    protected Image image; //the loaded image
    protected int width = -1;
    protected int height = -1;

    protected ImageObserver observer; //assigned observer (if used)
    protected Container container; //component's container, for repaint
    protected Toolkit toolkit; //system default toolkit
    protected boolean abort; //error or abort flag during loading in image observer
    
    public ContentImage(ElementBox owner)
    {
        super(owner);
        this.loadImages = true;
        this.observer = this;
        this.toolkit = Toolkit.getDefaultToolkit();
        this.caching = true;
        this.container = null;
        this.abort = false;
    }

    /**
     * Sets the component's container used by internal {@link ImageObserver}
     * when there is need to repaint (e.g. animated gif). If not set, rendering
     * may not work correctly. Null does not cause {@link NullPointerException}
     * 
     * @param container
     *            the new container
     * @see ImageObserver
     * @see Container
     */
    public void setContainer(Container container)
    {
        this.container = container;
    }

    /**
     * Gets the container. May return null.
     * 
     * @return the container
     */
    public Container getContainer()
    {
        return this.container;
    }

    /**
     * Fires repaint event within t milliseconds.
     * 
     * @param t
     *            the time to repaint within.
     * 
     * @see java.awt.Component#repaint(long, int, int, int, int)
     */
    protected void repaint(int t)
    {
        if (container != null)
        {
            Rectangle bounds = owner.getAbsoluteBounds();
            container.repaint(t, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    protected Image loadImage(boolean cache)
    {
        Image img;
        if (url != null)
        {
            if (cache)
            {
                // get image and cache
                img = toolkit.getImage(url);
            }
            else
            {
                // do not cache, just get image
                img = toolkit.createImage(url);
            }

            // start loading and preparation
            toolkit.prepareImage(img, -1, -1, observer);
            return img;
        }
        return null;
    }

    /**
     * Checks if caching is used when loading images.
     * 
     * @return true, if caching is used
     * 
     * @see ReplacedImage#setCaching(boolean)
     */
    public boolean isCaching()
    {
        return caching;
    }

    /**
     * Sets the caching. If set to true, image is checked, if present in cache.
     * If not, it is loaded and placed to cache. If set to False image is just
     * loaded, any cache and caching is avoided.
     * 
     * @param val
     *            indicates, whether use cache. Default is true.
     * @see Toolkit#getImage(URL)
     * @see Toolkit#createImage(URL)
     */
    public void setCaching(boolean val)
    {
        caching = val;
    }

    /**
     * Gets the url.
     * 
     * @return the url of the image.
     */
    public URL getUrl()
    {
        return url;
    }

    /**
     * Gets the image.
     * 
     * @return the image.
     */
    public Image getImage()
    {
        return image;
    }

    /**
     * Gets the loaded image as BufferedImage object. May return null if no data
     * available.
     * 
     * @return the buffered image.
     */
    public BufferedImage getBufferedImage()
    {
        if (image == null || abort)
            return null;
        
        BufferedImage img = new BufferedImage(getIntrinsicWidth(), getIntrinsicHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.drawImage(image, null, null);
        g.dispose();

        return img;
    }

    /**
     * Sets the image observer.
     * 
     * @param observer
     *            the new image observer.
     * @see ImageObserver
     */
    public void setImageObserver(ImageObserver observer)
    {
        this.observer = observer;
    }

    /**
     * Gets the image observer.
     * 
     * @return the image observer
     * @see ImageObserver
     */
    public ImageObserver getImageObserver()
    {
        return this.observer;
    }

    @Override
    public int getIntrinsicHeight()
    {
        if (height > -1)
        {
            return height; //previously computed height
        }
        else
        {
            if (loadImages)
            {
                if (image == null)
                    image = loadImage(caching);
                height = obtainImageHeight();
                return height;
            }
            else
            {
                return DEFAULT_IMAGE_HEIGHT;
            }
        }
    }

    @Override
    public int getIntrinsicWidth()
    {
        if (width > -1)
        {
            return width; //previously computed width
        }
        else
        {
            if (loadImages)
            {
                if (image == null)
                    image = loadImage(caching);
                width = obtainImageWidth();
                return width;// if there was an error, height == DEFAULT_IMAGE_HEIGHT
            }
            else
            {
                return DEFAULT_IMAGE_WIDTH;
            }
        }
    }

    private int obtainImageWidth()
    {
        int width = -1;
        abort = false;
        int loadtime = 0;
        while (!abort && image != null && (width = image.getWidth(observer)) == -1)
        {
            try
            {
                if (loadtime > LOAD_TIMEOUT)
                {
                    image = null;
                    abort = true;
                }
                Thread.sleep(25);
                loadtime += 25;
            } catch (Exception e)
            {
                image = null;
                abort = true;
            }
        }
        if (width == -1) width = DEFAULT_IMAGE_WIDTH;
        return width;
    }

    private int obtainImageHeight()
    {
        int height = -1;
        abort = false;
        int loadtime = 0;
        while (!abort && image != null && (height = image.getHeight(observer)) == -1)
        {
            try
            {
                if (loadtime > LOAD_TIMEOUT)
                {
                    image = null;
                    abort = true;
                }
                else
                    Thread.sleep(25);
                loadtime += 25;
            } catch (Exception e)
            {
                image = null;
                abort = true;
            }
        }

        if (height == -1) height = DEFAULT_IMAGE_HEIGHT;
        return height;
    }
    
    @Override
    public float getIntrinsicRatio()
    {
        return (float) getIntrinsicWidth() / (float) getIntrinsicHeight();
    }

    /**
     * Releases the image resources.
     */
    public void dispose()
    {
        if (image != null) image.flush();
        image = null;
    }

    /**
     * Resets all data to default state, releases the image resources.
     */
    public void reset()
    {
        abort = false;
        width = -1;
        height = -1;
        if (image != null) image.flush();
        image = null;
    }

    /**
     * Aborts waiting for image data to determine width and/or height. This
     * method is called from ImageObserver. If custom ImageObserver is provided
     * and ABORT or ERROR flags are set during loading, call this method to
     * abort waiting. Otherwise infinite loop will be created, resulting in
     * dead-lock.
     * 
     * @see ImageObserver
     * @see ImageObserver#ABORT
     * @see ImageObserver#ERROR
     */
    public synchronized void abort()
    {
        abort = true;
        image = null;
    }

    public boolean imageUpdate(Image img, int flags, int x, int y, int newWidth, int newHeight)
    {
        // http://www.permadi.com/tutorial/javaImgObserverAndAnimGif/
        if (image == null || image != img) { return false; }

        // error
        if ((flags & (ABORT | ERROR)) != 0)
        {
            synchronized (this)
            {
                if (image == img)
                {
                    abort();
                }
            }

            repaint(0);
            return false;
        }

        // Repaint when done or when new pixels arrive:
        // FRAMEBITS - another complete frame of a multi-frame image which was
        // previously drawn is now available to be drawn again.
        // ALLBITS - static image which was previously drawn is now complete and
        // can be drawn again in its final form.
        if ((flags & (FRAMEBITS | ALLBITS)) != 0)
        {
            repaint(0);
        }

        // hint : provide some "Loading..." animation...
        // else if ((flags & SOMEBITS) != 0) {}
        return ((flags & ALLBITS) == 0) /* && owner.isVisible() */;
    }

}
