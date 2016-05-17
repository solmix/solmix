/*
 * Copyright 2014 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */
package org.solmix.commons.util;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;



/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年6月23日
 */

public class AntMatcherTest
{
    private AntMatcher matcher= new AntMatcher();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void match() {
          // test exact matching
          assertTrue(matcher.match("test", "test"));
          assertTrue(matcher.match("/test", "/test"));
          assertFalse(matcher.match("/test.jpg", "test.jpg"));
          assertFalse(matcher.match("test", "/test"));
          assertFalse(matcher.match("/test", "test"));

          // test matching with ?'s
          assertTrue(matcher.match("t?st", "test"));
          assertTrue(matcher.match("??st", "test"));
          assertTrue(matcher.match("tes?", "test"));
          assertTrue(matcher.match("te??", "test"));
          assertTrue(matcher.match("?es?", "test"));
          assertFalse(matcher.match("tes?", "tes"));
          assertFalse(matcher.match("tes?", "testt"));
          assertFalse(matcher.match("tes?", "tsst"));

          // test matching with *'s
          assertTrue(matcher.match("*", "test"));
          assertTrue(matcher.match("test*", "test"));
          assertTrue(matcher.match("test*", "testTest"));
          assertTrue(matcher.match("test/*", "test/Test"));
          assertTrue(matcher.match("test/*", "test/t"));
          assertTrue(matcher.match("test/*", "test/"));
          assertTrue(matcher.match("*test*", "AnothertestTest"));
          assertTrue(matcher.match("*test", "Anothertest"));
          assertTrue(matcher.match("*.*", "test."));
          assertTrue(matcher.match("*.*", "test.test"));
          assertTrue(matcher.match("*.*", "test.test.test"));
          assertTrue(matcher.match("test*aaa", "testblaaaa"));
          assertFalse(matcher.match("test*", "tst"));
          assertFalse(matcher.match("test*", "tsttest"));
          assertFalse(matcher.match("test*", "test/"));
          assertFalse(matcher.match("test*", "test/t"));
          assertFalse(matcher.match("test/*", "test"));
          assertFalse(matcher.match("*test*", "tsttst"));
          assertFalse(matcher.match("*test", "tsttst"));
          assertFalse(matcher.match("*.*", "tsttst"));
          assertFalse(matcher.match("test*aaa", "test"));
          assertFalse(matcher.match("test*aaa", "testblaaab"));

          // test matching with ?'s and /'s
          assertTrue(matcher.match("/?", "/a"));
          assertTrue(matcher.match("/?/a", "/a/a"));
          assertTrue(matcher.match("/a/?", "/a/b"));
          assertTrue(matcher.match("/??/a", "/aa/a"));
          assertTrue(matcher.match("/a/??", "/a/bb"));
          assertTrue(matcher.match("/?", "/a"));

          // test matching with **'s
          assertTrue(matcher.match("/**", "/testing/testing"));
          assertTrue(matcher.match("/*/**", "/testing/testing"));
          assertTrue(matcher.match("/**/*", "/testing/testing"));
          assertTrue(matcher.match("/bla/**/bla", "/bla/testing/testing/bla"));
          assertTrue(matcher.match("/bla/**/bla", "/bla/testing/testing/bla/bla"));
          assertTrue(matcher.match("/**/test", "/bla/bla/test"));
          assertTrue(matcher.match("/bla/**/**/bla", "/bla/bla/bla/bla/bla/bla"));
          assertTrue(matcher.match("/bla*bla/test", "/blaXXXbla/test"));
          assertTrue(matcher.match("/*bla/test", "/XXXbla/test"));
          assertFalse(matcher.match("/bla*bla/test", "/blaXXXbl/test"));
          assertFalse(matcher.match("/*bla/test", "XXXblab/test"));
          assertFalse(matcher.match("/*bla/test", "XXXbl/test"));

          assertFalse(matcher.match("/????", "/bala/bla"));
          assertFalse(matcher.match("/**/*bla", "/bla/bla/bla/bbb"));

          assertTrue(matcher.match("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing/"));
          assertTrue(matcher.match("/*bla*/**/bla/*", "/XXXblaXXXX/testing/testing/bla/testing"));
          assertTrue(matcher.match("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing"));
          assertTrue(matcher.match("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing.jpg"));

          assertTrue(matcher.match("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing/"));
          assertTrue(matcher.match("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing"));
          assertTrue(matcher.match("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing"));
          assertFalse(matcher.match("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing/testing"));

          assertFalse(matcher.match("/x/x/**/bla", "/x/x/x/"));

          assertTrue(matcher.match("", ""));

          assertTrue(matcher.match("/{bla}.*", "/testing.html"));
    }
    
    @Test
    public void withMatchStart() {
    // test matching with ?'s
    assertTrue(matcher.matchStart("t?st", "test"));
    assertTrue(matcher.matchStart("??st", "test"));
    assertTrue(matcher.matchStart("tes?", "test"));
    assertTrue(matcher.matchStart("te??", "test"));
    assertTrue(matcher.matchStart("?es?", "test"));
    assertFalse(matcher.matchStart("tes?", "tes"));
    assertFalse(matcher.matchStart("tes?", "testt"));
    assertFalse(matcher.matchStart("tes?", "tsst"));

    // test matching with *'s
    assertTrue(matcher.matchStart("*", "test"));
    assertTrue(matcher.matchStart("test*", "test"));
    assertTrue(matcher.matchStart("test*", "testTest"));
    assertTrue(matcher.matchStart("test/*", "test/Test"));
    assertTrue(matcher.matchStart("test/*", "test/t"));
    assertTrue(matcher.matchStart("test/*", "test/"));
    assertTrue(matcher.matchStart("*test*", "AnothertestTest"));
    assertTrue(matcher.matchStart("*test", "Anothertest"));
    assertTrue(matcher.matchStart("*.*", "test."));
    assertTrue(matcher.matchStart("*.*", "test.test"));
    assertTrue(matcher.matchStart("*.*", "test.test.test"));
    assertTrue(matcher.matchStart("test*aaa", "testblaaaa"));
    assertFalse(matcher.matchStart("test*", "tst"));
    assertFalse(matcher.matchStart("test*", "test/"));
    assertFalse(matcher.matchStart("test*", "tsttest"));
    assertFalse(matcher.matchStart("test*", "test/"));
    assertFalse(matcher.matchStart("test*", "test/t"));
    assertTrue(matcher.matchStart("test/*", "test"));
    assertTrue(matcher.matchStart("test/t*.txt", "test"));
    assertFalse(matcher.matchStart("*test*", "tsttst"));
    assertFalse(matcher.matchStart("*test", "tsttst"));
    assertFalse(matcher.matchStart("*.*", "tsttst"));
    assertFalse(matcher.matchStart("test*aaa", "test"));
    assertFalse(matcher.matchStart("test*aaa", "testblaaab"));

    // test matching with ?'s and /'s
    assertTrue(matcher.matchStart("/?", "/a"));
    assertTrue(matcher.matchStart("/?/a", "/a/a"));
    assertTrue(matcher.matchStart("/a/?", "/a/b"));
    assertTrue(matcher.matchStart("/??/a", "/aa/a"));
    assertTrue(matcher.matchStart("/a/??", "/a/bb"));
    assertTrue(matcher.matchStart("/?", "/a"));

    // test matching with **'s
    assertTrue(matcher.matchStart("/**", "/testing/testing"));
    assertTrue(matcher.matchStart("/*/**", "/testing/testing"));
    assertTrue(matcher.matchStart("/**/*", "/testing/testing"));
    assertTrue(matcher.matchStart("test*/**", "test/"));
    assertTrue(matcher.matchStart("test*/**", "test/t"));
    assertTrue(matcher.matchStart("/bla/**/bla", "/bla/testing/testing/bla"));
    assertTrue(matcher.matchStart("/bla/**/bla", "/bla/testing/testing/bla/bla"));
    assertTrue(matcher.matchStart("/**/test", "/bla/bla/test"));
    assertTrue(matcher.matchStart("/bla/**/**/bla", "/bla/bla/bla/bla/bla/bla"));
    assertTrue(matcher.matchStart("/bla*bla/test", "/blaXXXbla/test"));
    assertTrue(matcher.matchStart("/*bla/test", "/XXXbla/test"));
    assertFalse(matcher.matchStart("/bla*bla/test", "/blaXXXbl/test"));
    assertFalse(matcher.matchStart("/*bla/test", "XXXblab/test"));
    assertFalse(matcher.matchStart("/*bla/test", "XXXbl/test"));

    assertFalse(matcher.matchStart("/????", "/bala/bla"));
    assertTrue(matcher.matchStart("/**/*bla", "/bla/bla/bla/bbb"));

    assertTrue(matcher.matchStart("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing/"));
    assertTrue(matcher.matchStart("/*bla*/**/bla/*", "/XXXblaXXXX/testing/testing/bla/testing"));
    assertTrue(matcher.matchStart("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing"));
    assertTrue(matcher.matchStart("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing.jpg"));

    assertTrue(matcher.matchStart("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing/"));
    assertTrue(matcher.matchStart("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing"));
    assertTrue(matcher.matchStart("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing"));
    assertTrue(matcher.matchStart("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing/testing"));

    assertTrue(matcher.matchStart("/x/x/**/bla", "/x/x/x/"));

    assertTrue(matcher.matchStart("", ""));
}

@Test
public void uniqueDeliminator() {
    matcher.setPathSeparator(".");

    // test exact matching
    assertTrue(matcher.match("test", "test"));
    assertTrue(matcher.match(".test", ".test"));
    assertFalse(matcher.match(".test/jpg", "test/jpg"));
    assertFalse(matcher.match("test", ".test"));
    assertFalse(matcher.match(".test", "test"));

    // test matching with ?'s
    assertTrue(matcher.match("t?st", "test"));
    assertTrue(matcher.match("??st", "test"));
    assertTrue(matcher.match("tes?", "test"));
    assertTrue(matcher.match("te??", "test"));
    assertTrue(matcher.match("?es?", "test"));
    assertFalse(matcher.match("tes?", "tes"));
    assertFalse(matcher.match("tes?", "testt"));
    assertFalse(matcher.match("tes?", "tsst"));

    // test matching with *'s
    assertTrue(matcher.match("*", "test"));
    assertTrue(matcher.match("test*", "test"));
    assertTrue(matcher.match("test*", "testTest"));
    assertTrue(matcher.match("*test*", "AnothertestTest"));
    assertTrue(matcher.match("*test", "Anothertest"));
    assertTrue(matcher.match("*/*", "test/"));
    assertTrue(matcher.match("*/*", "test/test"));
    assertTrue(matcher.match("*/*", "test/test/test"));
    assertTrue(matcher.match("test*aaa", "testblaaaa"));
    assertFalse(matcher.match("test*", "tst"));
    assertFalse(matcher.match("test*", "tsttest"));
    assertFalse(matcher.match("*test*", "tsttst"));
    assertFalse(matcher.match("*test", "tsttst"));
    assertFalse(matcher.match("*/*", "tsttst"));
    assertFalse(matcher.match("test*aaa", "test"));
    assertFalse(matcher.match("test*aaa", "testblaaab"));

    // test matching with ?'s and .'s
    assertTrue(matcher.match(".?", ".a"));
    assertTrue(matcher.match(".?.a", ".a.a"));
    assertTrue(matcher.match(".a.?", ".a.b"));
    assertTrue(matcher.match(".??.a", ".aa.a"));
    assertTrue(matcher.match(".a.??", ".a.bb"));
    assertTrue(matcher.match(".?", ".a"));

    // test matching with **'s
    assertTrue(matcher.match(".**", ".testing.testing"));
    assertTrue(matcher.match(".*.**", ".testing.testing"));
    assertTrue(matcher.match(".**.*", ".testing.testing"));
    assertTrue(matcher.match(".bla.**.bla", ".bla.testing.testing.bla"));
    assertTrue(matcher.match(".bla.**.bla", ".bla.testing.testing.bla.bla"));
    assertTrue(matcher.match(".**.test", ".bla.bla.test"));
    assertTrue(matcher.match(".bla.**.**.bla", ".bla.bla.bla.bla.bla.bla"));
    assertTrue(matcher.match(".bla*bla.test", ".blaXXXbla.test"));
    assertTrue(matcher.match(".*bla.test", ".XXXbla.test"));
    assertFalse(matcher.match(".bla*bla.test", ".blaXXXbl.test"));
    assertFalse(matcher.match(".*bla.test", "XXXblab.test"));
    assertFalse(matcher.match(".*bla.test", "XXXbl.test"));
}

@Test
public void extractPathWithinPattern() throws Exception {
    assertEquals("", matcher.extractPathWithinPattern("/docs/commit.html", "/docs/commit.html"));

    assertEquals("cvs/commit", matcher.extractPathWithinPattern("/docs/*", "/docs/cvs/commit"));
    assertEquals("commit.html", matcher.extractPathWithinPattern("/docs/cvs/*.html", "/docs/cvs/commit.html"));
    assertEquals("cvs/commit", matcher.extractPathWithinPattern("/docs/**", "/docs/cvs/commit"));
    assertEquals("cvs/commit.html",
                matcher.extractPathWithinPattern("/docs/**/*.html", "/docs/cvs/commit.html"));
    assertEquals("commit.html", matcher.extractPathWithinPattern("/docs/**/*.html", "/docs/commit.html"));
    assertEquals("commit.html", matcher.extractPathWithinPattern("/*.html", "/commit.html"));
    assertEquals("docs/commit.html", matcher.extractPathWithinPattern("/*.html", "/docs/commit.html"));
    assertEquals("/commit.html", matcher.extractPathWithinPattern("*.html", "/commit.html"));
    assertEquals("/docs/commit.html", matcher.extractPathWithinPattern("*.html", "/docs/commit.html"));
    assertEquals("/docs/commit.html", matcher.extractPathWithinPattern("**/*.*", "/docs/commit.html"));
    assertEquals("/docs/commit.html", matcher.extractPathWithinPattern("*", "/docs/commit.html"));
//    assertEquals("/docs/cvs/other/commit.html", matcher.extractPathWithinPattern("**/commit.html", "/docs/cvs/other/commit.html"));
//    assertEquals("cvs/other/commit.html", matcher.extractPathWithinPattern("/docs/**/commit.html", "/docs/cvs/other/commit.html"));
    assertEquals("cvs/other/commit.html", matcher.extractPathWithinPattern("/docs/**/**/**/**", "/docs/cvs/other/commit.html"));

    assertEquals("docs/cvs/commit", matcher.extractPathWithinPattern("/d?cs/*", "/docs/cvs/commit"));
    assertEquals("cvs/commit.html",
                matcher.extractPathWithinPattern("/docs/c?s/*.html", "/docs/cvs/commit.html"));
    assertEquals("docs/cvs/commit", matcher.extractPathWithinPattern("/d?cs/**", "/docs/cvs/commit"));
    assertEquals("docs/cvs/commit.html",
                matcher.extractPathWithinPattern("/d?cs/**/*.html", "/docs/cvs/commit.html"));
}

@Test
public void extractUriTemplateVariables() throws Exception {
    Map<String, String> result = matcher.extractUriTemplateVariables("/hotels/{hotel}", "/hotels/1");
    assertEquals(Collections.singletonMap("hotel", "1"), result);

    result = matcher.extractUriTemplateVariables("/h?tels/{hotel}", "/hotels/1");
    assertEquals(Collections.singletonMap("hotel", "1"), result);

    result = matcher.extractUriTemplateVariables("/hotels/{hotel}/bookings/{booking}", "/hotels/1/bookings/2");
    Map<String, String> expected = new LinkedHashMap<String, String>();
    expected.put("hotel", "1");
    expected.put("booking", "2");
    assertEquals(expected, result);

    result = matcher.extractUriTemplateVariables("/**/hotels/**/{hotel}", "/foo/hotels/bar/1");
    assertEquals(Collections.singletonMap("hotel", "1"), result);

    result = matcher.extractUriTemplateVariables("/{page}.html", "/42.html");
    assertEquals(Collections.singletonMap("page", "42"), result);

    result = matcher.extractUriTemplateVariables("/{page}.*", "/42.html");
    assertEquals(Collections.singletonMap("page", "42"), result);

    result = matcher.extractUriTemplateVariables("/A-{B}-C", "/A-b-C");
    assertEquals(Collections.singletonMap("B", "b"), result);

    result = matcher.extractUriTemplateVariables("/{name}.{extension}", "/test.html");
    expected = new LinkedHashMap<String, String>();
    expected.put("name", "test");
    expected.put("extension", "html");
    assertEquals(expected, result);
}

@Test
public void extractUriTemplateVariablesRegex() {
    Map<String, String> result = matcher
                .extractUriTemplateVariables("{symbolicName:[\\w\\.]+}-{version:[\\w\\.]+}.jar",
                            "com.example-1.0.0.jar");
    assertEquals("com.example", result.get("symbolicName"));
    assertEquals("1.0.0", result.get("version"));

    result = matcher.extractUriTemplateVariables("{symbolicName:[\\w\\.]+}-sources-{version:[\\w\\.]+}.jar",
                "com.example-sources-1.0.0.jar");
    assertEquals("com.example", result.get("symbolicName"));
    assertEquals("1.0.0", result.get("version"));
}


@Test
public void extractUriTemplateVarsRegexQualifiers() {
    Map<String, String> result = matcher.extractUriTemplateVariables(
                "{symbolicName:[\\p{L}\\.]+}-sources-{version:[\\p{N}\\.]+}.jar",
                "com.example-sources-1.0.0.jar");
    assertEquals("com.example", result.get("symbolicName"));
    assertEquals("1.0.0", result.get("version"));

    result = matcher.extractUriTemplateVariables(
                "{symbolicName:[\\w\\.]+}-sources-{version:[\\d\\.]+}-{year:\\d{4}}{month:\\d{2}}{day:\\d{2}}.jar",
                "com.example-sources-1.0.0-20100220.jar");
    assertEquals("com.example", result.get("symbolicName"));
    assertEquals("1.0.0", result.get("version"));
    assertEquals("2010", result.get("year"));
    assertEquals("02", result.get("month"));
    assertEquals("20", result.get("day"));

    result = matcher.extractUriTemplateVariables(
                "{symbolicName:[\\p{L}\\.]+}-sources-{version:[\\p{N}\\.\\{\\}]+}.jar",
                "com.example-sources-1.0.0.{12}.jar");
    assertEquals("com.example", result.get("symbolicName"));
    assertEquals("1.0.0.{12}", result.get("version"));
}

/**
* SPR-8455
*/
@Test
public void extractUriTemplateVarsRegexCapturingGroups() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage(containsString("The number of capturing groups in the pattern"));
    matcher.extractUriTemplateVariables("/web/{id:foo(bar)?}", "/web/foobar");
}

@Test
public void combine() {
    assertEquals("", matcher.combine(null, null));
    assertEquals("/hotels", matcher.combine("/hotels", null));
    assertEquals("/hotels", matcher.combine(null, "/hotels"));
    assertEquals("/hotels/booking", matcher.combine("/hotels/*", "booking"));
    assertEquals("/hotels/booking", matcher.combine("/hotels/*", "/booking"));
    assertEquals("/hotels/**/booking", matcher.combine("/hotels/**", "booking"));
    assertEquals("/hotels/**/booking", matcher.combine("/hotels/**", "/booking"));
    assertEquals("/hotels/booking", matcher.combine("/hotels", "/booking"));
    assertEquals("/hotels/booking", matcher.combine("/hotels", "booking"));
    assertEquals("/hotels/booking", matcher.combine("/hotels/", "booking"));
    assertEquals("/hotels/{hotel}", matcher.combine("/hotels/*", "{hotel}"));
    assertEquals("/hotels/**/{hotel}", matcher.combine("/hotels/**", "{hotel}"));
    assertEquals("/hotels/{hotel}", matcher.combine("/hotels", "{hotel}"));
    assertEquals("/hotels/{hotel}.*", matcher.combine("/hotels", "{hotel}.*"));
    assertEquals("/hotels/*/booking/{booking}", matcher.combine("/hotels/*/booking", "{booking}"));
    assertEquals("/hotel.html", matcher.combine("/*.html", "/hotel.html"));
    assertEquals("/hotel.html", matcher.combine("/*.html", "/hotel"));
    assertEquals("/hotel.html", matcher.combine("/*.html", "/hotel.*"));
    assertEquals("/*.html", matcher.combine("/**", "/*.html"));
    assertEquals("/*.html", matcher.combine("/*", "/*.html"));
    assertEquals("/*.html", matcher.combine("/*.*", "/*.html"));
    assertEquals("/{foo}/bar", matcher.combine("/{foo}", "/bar"));      
    assertEquals("/user/user", matcher.combine("/user", "/user"));     
    assertEquals("/{foo:.*[^0-9].*}/edit/", matcher.combine("/{foo:.*[^0-9].*}", "/edit/")); 
    assertEquals("/1.0/foo/test", matcher.combine("/1.0", "/foo/test")); 
    assertEquals("/hotel", matcher.combine("/", "/hotel")); 
    assertEquals("/hotel/booking", matcher.combine("/hotel/", "/booking")); 
}

@Test
public void combineWithTwoFileExtensionPatterns() {
    exception.expect(IllegalArgumentException.class);
    matcher.combine("/*.html", "/*.txt");
}

@Test
public void patternComparator() {
    Comparator<String> comparator = matcher.getPatternComparator("/hotels/new");

    assertEquals(0, comparator.compare(null, null));
    assertEquals(1, comparator.compare(null, "/hotels/new"));
    assertEquals(-1, comparator.compare("/hotels/new", null));

    assertEquals(0, comparator.compare("/hotels/new", "/hotels/new"));

    assertEquals(-1, comparator.compare("/hotels/new", "/hotels/*"));
    assertEquals(1, comparator.compare("/hotels/*", "/hotels/new"));
    assertEquals(0, comparator.compare("/hotels/*", "/hotels/*"));

    assertEquals(-1, comparator.compare("/hotels/new", "/hotels/{hotel}"));
    assertEquals(1, comparator.compare("/hotels/{hotel}", "/hotels/new"));
    assertEquals(0, comparator.compare("/hotels/{hotel}", "/hotels/{hotel}"));
    assertEquals(-1, comparator.compare("/hotels/{hotel}/booking", "/hotels/{hotel}/bookings/{booking}"));
    assertEquals(1, comparator.compare("/hotels/{hotel}/bookings/{booking}", "/hotels/{hotel}/booking"));

    assertEquals(-1, comparator.compare("/hotels/{hotel}/bookings/{booking}/cutomers/{customer}", "/**"));
    assertEquals(1, comparator.compare("/**","/hotels/{hotel}/bookings/{booking}/cutomers/{customer}"));
    assertEquals(0, comparator.compare("/**","/**"));

    assertEquals(-1, comparator.compare("/hotels/{hotel}", "/hotels/*"));
    assertEquals(1, comparator.compare("/hotels/*", "/hotels/{hotel}"));

//    assertEquals(-1, comparator.compare("/hotels/*", "/hotels/*/**"));
//    assertEquals(1, comparator.compare("/hotels/*/**", "/hotels/*"));

    assertEquals(-1, comparator.compare("/hotels/new", "/hotels/new.*"));
    assertEquals(2, comparator.compare("/hotels/{hotel}", "/hotels/{hotel}.*"));

//    assertEquals(-1, comparator.compare("/hotels/{hotel}/bookings/{booking}/cutomers/{customer}", "/hotels/**"));
//    assertEquals(1, comparator.compare("/hotels/**", "/hotels/{hotel}/bookings/{booking}/cutomers/{customer}"));
//    assertEquals(1, comparator.compare("/hotels/foo/bar/**", "/hotels/{hotel}"));
//    assertEquals(-1, comparator.compare("/hotels/{hotel}", "/hotels/foo/bar/**"));
//    assertEquals(2, comparator.compare("/hotels/**/bookings/**", "/hotels/**"));
//    assertEquals(-2, comparator.compare("/hotels/**", "/hotels/**/bookings/**"));

    //SPR-8683
    assertEquals(1, comparator.compare("/**", "/hotels/{hotel}"));

    // longer is better
    assertEquals(1, comparator.compare("/hotels", "/hotels2"));

//    assertEquals(-1, comparator.compare("*", "*/**"));
//    assertEquals(1, comparator.compare("*/**", "*"));
}

@Test
public void patternComparatorSort() {
    Comparator<String> comparator = matcher.getPatternComparator("/hotels/new");
    List<String> paths = new ArrayList<String>(3);

    paths.add(null);
    paths.add("/hotels/new");
    Collections.sort(paths, comparator);
    assertEquals("/hotels/new", paths.get(0));
    assertNull(paths.get(1));
    paths.clear();

    paths.add("/hotels/new");
    paths.add(null);
    Collections.sort(paths, comparator);
    assertEquals("/hotels/new", paths.get(0));
    assertNull(paths.get(1));
    paths.clear();

    paths.add("/hotels/*");
    paths.add("/hotels/new");
    Collections.sort(paths, comparator);
    assertEquals("/hotels/new", paths.get(0));
    assertEquals("/hotels/*", paths.get(1));
    paths.clear();

    paths.add("/hotels/new");
    paths.add("/hotels/*");
    Collections.sort(paths, comparator);
    assertEquals("/hotels/new", paths.get(0));
    assertEquals("/hotels/*", paths.get(1));
    paths.clear();

    paths.add("/hotels/**");
    paths.add("/hotels/*");
    Collections.sort(paths, comparator);
    assertEquals("/hotels/*", paths.get(0));
    assertEquals("/hotels/**", paths.get(1));
    paths.clear();

    paths.add("/hotels/*");
    paths.add("/hotels/**");
    Collections.sort(paths, comparator);
    assertEquals("/hotels/*", paths.get(0));
    assertEquals("/hotels/**", paths.get(1));
    paths.clear();

    paths.add("/hotels/{hotel}");
    paths.add("/hotels/new");
    Collections.sort(paths, comparator);
    assertEquals("/hotels/new", paths.get(0));
    assertEquals("/hotels/{hotel}", paths.get(1));
    paths.clear();

    paths.add("/hotels/new");
    paths.add("/hotels/{hotel}");
    Collections.sort(paths, comparator);
    assertEquals("/hotels/new", paths.get(0));
    assertEquals("/hotels/{hotel}", paths.get(1));
    paths.clear();

    paths.add("/hotels/*");
    paths.add("/hotels/{hotel}");
    paths.add("/hotels/new");
    Collections.sort(paths, comparator);
    assertEquals("/hotels/new", paths.get(0));
    assertEquals("/hotels/{hotel}", paths.get(1));
    assertEquals("/hotels/*", paths.get(2));
    paths.clear();

    paths.add("/hotels/ne*");
    paths.add("/hotels/n*");
    Collections.shuffle(paths);
    Collections.sort(paths, comparator);
    assertEquals("/hotels/ne*", paths.get(0));
    assertEquals("/hotels/n*", paths.get(1));
    paths.clear();

    comparator = matcher.getPatternComparator("/hotels/new.html");
    paths.add("/hotels/new.*");
    paths.add("/hotels/{hotel}");
    Collections.shuffle(paths);
    Collections.sort(paths, comparator);
    assertEquals("/hotels/new.*", paths.get(0));
    assertEquals("/hotels/{hotel}", paths.get(1));
    paths.clear();

    comparator = matcher.getPatternComparator("/web/endUser/action/login.html");
    paths.add("/**/login.*");
    paths.add("/**/endUser/action/login.*");
    Collections.sort(paths, comparator);
    assertEquals("/**/endUser/action/login.*", paths.get(0));
    assertEquals("/**/login.*", paths.get(1));
    paths.clear();
}

/**
* SPR-8687
*/
@Test
public void trimTokensOff() {
    matcher.setTrimTokens(false);

    assertTrue(matcher.match("/group/{groupName}/members", "/group/sales/members"));
    assertTrue(matcher.match("/group/{groupName}/members", "/group/  sales/members"));
}

@Test
public void defaultCacheSetting() {
    match();
    assertTrue(matcher.stringMatcherCache.size() > 20);

    for (int i = 0; i < 65536; i++) {
          matcher.match("test" + i, "test");
    }
    // Cache turned off because it went beyond the threshold
    assertTrue(matcher.stringMatcherCache.isEmpty());
}

@Test
public void cachePatternsSetToTrue() {
    matcher.setCachePatterns(true);
    match();
    assertTrue(matcher.stringMatcherCache.size() > 20);

    for (int i = 0; i < 65536; i++) {
          matcher.match("test" + i, "test");
    }
    // Cache keeps being alive due to the explicit cache setting
    assertTrue(matcher.stringMatcherCache.size() > 65536);
}

@Test
public void cachePatternsSetToFalse() {
    matcher.setCachePatterns(false);
    match();
    assertTrue(matcher.stringMatcherCache.isEmpty());
}

@Test
public void extensionMappingWithDotPathSeparator() {
    matcher.setPathSeparator(".");
    assertEquals("Extension mapping should be disabled with \".\" as path separator",
                "/*.html.hotel.*", matcher.combine("/*.html", "hotel.*"));
}
}
