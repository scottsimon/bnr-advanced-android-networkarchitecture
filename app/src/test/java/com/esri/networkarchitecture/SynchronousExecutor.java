/*
  COPYRIGHT 1995-2015  ESRI

  TRADE SECRETS: ESRI PROPRIETARY AND CONFIDENTIAL
  Unpublished material - all rights reserved under the
  Copyright Laws of the United States.

  For additional information, contact:
  Environmental Systems Research Institute, Inc.
  Attn: Contracts Dept
  380 New York Street
  Redlands, California, USA 92373

  email: contracts@esri.com
*/
package com.esri.networkarchitecture;

import java.util.concurrent.Executor;

/**
 * Created by scotts on 10/20/15.
 */
public class SynchronousExecutor implements Executor {
  @Override
  public void execute(Runnable command) {
    command.run();
  }
}
