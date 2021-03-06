/**
 * Copyright (c) 2015-present, Horcrux.
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

#import "RNSVGBrush.h"

#import "RCTDefines.h"

@implementation RNSVGBrush

- (instancetype)initWithArray:(NSArray *)data
{
    return [super init];
}

RCT_NOT_IMPLEMENTED(- (instancetype)init)

- (BOOL)applyFillColor:(CGContextRef)context
{
    return NO;
}

- (BOOL)applyStrokeColor:(CGContextRef)context
{
    return NO;
}

- (void)paint:(CGContextRef)context
{
    // abstract
}

@end
