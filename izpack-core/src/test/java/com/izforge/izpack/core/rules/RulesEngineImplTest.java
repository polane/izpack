package com.izforge.izpack.core.rules;


import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.adaptator.IXMLParser;
import com.izforge.izpack.api.adaptator.impl.XMLParser;
import com.izforge.izpack.api.data.AutomatedInstallData;
import com.izforge.izpack.api.rules.Condition;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.core.container.DefaultContainer;
import com.izforge.izpack.core.data.DefaultVariables;
import com.izforge.izpack.core.rules.logic.AndCondition;
import com.izforge.izpack.core.rules.logic.NotCondition;
import com.izforge.izpack.core.rules.logic.OrCondition;
import com.izforge.izpack.core.rules.logic.XorCondition;
import com.izforge.izpack.core.rules.process.CompareNumericsCondition;
import com.izforge.izpack.core.rules.process.CompareVersionsCondition;
import com.izforge.izpack.core.rules.process.EmptyCondition;
import com.izforge.izpack.core.rules.process.ExistsCondition;
import com.izforge.izpack.core.rules.process.JavaCondition;
import com.izforge.izpack.core.rules.process.PackSelectionCondition;
import com.izforge.izpack.core.rules.process.RefCondition;
import com.izforge.izpack.core.rules.process.UserCondition;
import com.izforge.izpack.core.rules.process.VariableCondition;
import com.izforge.izpack.util.Platform;
import com.izforge.izpack.util.Platforms;


public class RulesEngineImplTest
{
    private RulesEngine engine = null;

    /**
     * AIX install condition identifier.
     */
    private static final String AIX_INSTALL = "izpack.aixinstall";

    /**
     * Windows install condition identifier.
     */
    private static final String WINDOWS_INSTALL = "izpack.windowsinstall";

    /**
     * Windows XP install condition identifier.
     */
    private static final String WINDOWS_XP_INSTALL = "izpack.windowsinstall.xp";

    /**
     * Windows 2003 install condition identifier.
     */
    private static final String WINDOWS_2003_INSTALL = "izpack.windowsinstall.2003";

    /**
     * Windows Vista install condition identifier.
     */
    private static final String WINDOWS_VISTA_INSTALL = "izpack.windowsinstall.vista";

    /**
     * Windows 7 install condition identifier.
     */
    private static final String WINDOWS_7_INSTALL = "izpack.windowsinstall.7";

    /**
     * Linux install condition identifier.
     */
    private static final String LINUX_INSTALL = "izpack.linuxinstall";

    /**
     * Solaris install condition identifier.
     */
    private static final String SOLARIS_INSTALL = "izpack.solarisinstall";

    /**
     * Solaris x86 install condition identifier.
     */
    private static final String SOLARIS_X86_INSTALL = "izpack.solarisinstall.x86";

    /**
     * Solaris Sparc install condition identifier.
     */
    private static final String SOLARIS_SPARC_INSTALL = "izpack.solarisinstall.sparc";

    /**
     * Mac install condition identifier.
     */
    private static final String MAC_INSTALL = "izpack.macinstall";

    /**
     * OSX install condition identifier.
     */
    private static final String MAC_OSX_INSTALL = "izpack.macinstall.osx";

    /**
     * All install condition identifiers.
     */
    private static final String INSTALL_CONDITIONS[] = {AIX_INSTALL, WINDOWS_INSTALL, WINDOWS_XP_INSTALL,
            WINDOWS_2003_INSTALL, WINDOWS_VISTA_INSTALL, WINDOWS_7_INSTALL, LINUX_INSTALL, SOLARIS_INSTALL,
            SOLARIS_X86_INSTALL, SOLARIS_SPARC_INSTALL, MAC_INSTALL, MAC_OSX_INSTALL};


    @Before
    public void setUp() throws Exception
    {
        DefaultVariables variables = new DefaultVariables();
        engine = new RulesEngineImpl(new AutomatedInstallData(variables), null, Platforms.LINUX);
        variables.setRules(engine);

        Map<String, Condition> conditions = new HashMap<String, Condition>();
        Condition alwaysFalse = new JavaCondition();
        conditions.put("false", alwaysFalse);

        Condition alwaysTrue = NotCondition.createFromCondition(alwaysFalse, engine);
        conditions.put("true", alwaysTrue);

        engine.readConditionMap(conditions);
    }

    @Test
    @SuppressWarnings("PointlessBooleanExpression")
    public void testSimpleNot() throws Exception
    {
        Condition condition;

        condition = engine.getCondition("@!false");
        assertEquals(!false, condition.isTrue());

        condition = engine.getCondition("@!true");
        assertEquals(!true, condition.isTrue());
    }

    @Test
    @SuppressWarnings("PointlessBooleanExpression")
    public void testSimpleAnd() throws Exception
    {
        Condition condition;

        condition = engine.getCondition("@false && false");
        assertEquals(false && false, condition.isTrue());

        condition = engine.getCondition("@false && true");
        assertEquals(false && true, condition.isTrue());

        condition = engine.getCondition("@true && false");
        assertEquals(true && false, condition.isTrue());

        condition = engine.getCondition("@true && true");
        assertEquals(true && true, condition.isTrue());
    }

    @Test
    @SuppressWarnings("PointlessBooleanExpression")
    public void testSimpleOr() throws Exception
    {
        Condition condition;

        condition = engine.getCondition("@false || false");
        assertEquals(false || false, condition.isTrue());

        condition = engine.getCondition("@false || true");
        assertEquals(false || true, condition.isTrue());

        condition = engine.getCondition("@true || false");
        assertEquals(true || false, condition.isTrue());

        condition = engine.getCondition("@true || true");
        assertEquals(true || true, condition.isTrue());
    }

    @Test
    @SuppressWarnings("PointlessBooleanExpression")
    public void testSimpleXor() throws Exception
    {
        Condition condition;

        condition = engine.getCondition("@false ^ false");
        assertEquals(false ^ false, condition.isTrue());

        condition = engine.getCondition("@false ^ true");
        assertEquals(false ^ true, condition.isTrue());

        condition = engine.getCondition("@true ^ false");
        assertEquals(true ^ false, condition.isTrue());

        condition = engine.getCondition("@true ^ true");
        assertEquals(true ^ true, condition.isTrue());
    }


    @Test
    @SuppressWarnings("PointlessBooleanExpression")
    public void testComplexNot() throws Exception
    {
        Condition condition;

        condition = engine.getCondition("@!false || false");
        assertEquals(!false || false, condition.isTrue());

        condition = engine.getCondition("@!true || false");
        assertEquals(!true || false, condition.isTrue());

        condition = engine.getCondition("@false || !false");
        assertEquals(false || !false, condition.isTrue());

        condition = engine.getCondition("@true || !false");
        assertEquals(true || !false, condition.isTrue());

        condition = engine.getCondition("@!false && true");
        assertEquals(!false && true, condition.isTrue());

        condition = engine.getCondition("@true && !false");
        assertEquals(true && !false, condition.isTrue());

    }

    @Test
    @SuppressWarnings("PointlessBooleanExpression")
    public void testComplexAnd() throws Exception
    {
        Condition condition;

        condition = engine.getCondition("@false || false && false || false");
        assertEquals(false || false && false || false, condition.isTrue());

        condition = engine.getCondition("@false || false && false || true");
        assertEquals(false || false && false || true, condition.isTrue());

        condition = engine.getCondition("@false || false && true || false");
        assertEquals(false || false && true || false, condition.isTrue());

        condition = engine.getCondition("@false || false && true || true");
        assertEquals(false || false && true || true, condition.isTrue());

        condition = engine.getCondition("@false || true && false || false");
        assertEquals(false || true && false || false, condition.isTrue());

        condition = engine.getCondition("@false || true && false || false");
        assertEquals(false || true && false || false, condition.isTrue());

        condition = engine.getCondition("@false || true && false || true");
        assertEquals(false || true && false || true, condition.isTrue());

        condition = engine.getCondition("@false || true && true || false");
        assertEquals(false || true && true || false, condition.isTrue());

        condition = engine.getCondition("@false || true && true || true");
        assertEquals(false || true && true || true, condition.isTrue());

        condition = engine.getCondition("@true || false && false || false");
        assertEquals(true || false && false || false, condition.isTrue());

        condition = engine.getCondition("@true || false && false || true");
        assertEquals(true || false && false || true, condition.isTrue());

        condition = engine.getCondition("@true || false && true || false");
        assertEquals(true || false && true || false, condition.isTrue());

        condition = engine.getCondition("@true || false && true || true");
        assertEquals(true || false && true || true, condition.isTrue());

        condition = engine.getCondition("@true || true && false || false");
        assertEquals(true || true && false || false, condition.isTrue());

        condition = engine.getCondition("@true || true && false || false");
        assertEquals(true || true && false || false, condition.isTrue());

        condition = engine.getCondition("@true || true && false || true");
        assertEquals(true || true && false || true, condition.isTrue());

        condition = engine.getCondition("@true || true && true || false");
        assertEquals(true || true && true || false, condition.isTrue());

        condition = engine.getCondition("@true || true && true || true");
        assertEquals(true || true && true || true, condition.isTrue());

    }

    @Test
    @SuppressWarnings("PointlessBooleanExpression")
    public void testComplexOr() throws Exception
    {
        Condition condition;

        condition = engine.getCondition("@false && false || false && false");
        assertEquals(false && false || false && false, condition.isTrue());

        condition = engine.getCondition("@false && false || false && true");
        assertEquals(false && false || false && true, condition.isTrue());

        condition = engine.getCondition("@false && false || true && false");
        assertEquals(false && false || true && false, condition.isTrue());

        condition = engine.getCondition("@false && false || true && true");
        assertEquals(false && false || true && true, condition.isTrue());

        condition = engine.getCondition("@false && true || false && false");
        assertEquals(false && true || false && false, condition.isTrue());

        condition = engine.getCondition("@false && true || false && false");
        assertEquals(false && true || false && false, condition.isTrue());

        condition = engine.getCondition("@false && true || false && true");
        assertEquals(false && true || false && true, condition.isTrue());

        condition = engine.getCondition("@false && true || true && false");
        assertEquals(false && true || true && false, condition.isTrue());

        condition = engine.getCondition("@false && true || true && true");
        assertEquals(false && true || true && true, condition.isTrue());

        condition = engine.getCondition("@true && false || false && false");
        assertEquals(true && false || false && false, condition.isTrue());

        condition = engine.getCondition("@true && false || false && true");
        assertEquals(true && false || false && true, condition.isTrue());

        condition = engine.getCondition("@true && false || true && false");
        assertEquals(true && false || true && false, condition.isTrue());

        condition = engine.getCondition("@true && false || true && true");
        assertEquals(true && false || true && true, condition.isTrue());

        condition = engine.getCondition("@true && true || false && false");
        assertEquals(true && true || false && false, condition.isTrue());

        condition = engine.getCondition("@true && true || false && false");
        assertEquals(true && true || false && false, condition.isTrue());

        condition = engine.getCondition("@true && true || false && true");
        assertEquals(true && true || false && true, condition.isTrue());

        condition = engine.getCondition("@true && true || true && false");
        assertEquals(true && true || true && false, condition.isTrue());

        condition = engine.getCondition("@true && true || true && true");
        assertEquals(true && true || true && true, condition.isTrue());
    }

    @Test
    @SuppressWarnings("PointlessBooleanExpression")
    public void testComplexXor() throws Exception
    {
        Condition condition;

        condition = engine.getCondition("@false && false ^ false && false");
        assertEquals(false && false ^ false && false, condition.isTrue());

        condition = engine.getCondition("@false && false ^ false && true");
        assertEquals(false && false ^ false && true, condition.isTrue());

        condition = engine.getCondition("@false && false ^ true && false");
        assertEquals(false && false ^ true && false, condition.isTrue());

        condition = engine.getCondition("@false && false ^ true && true");
        assertEquals(false && false ^ true && true, condition.isTrue());

        condition = engine.getCondition("@false && true ^ false && false");
        assertEquals(false && true ^ false && false, condition.isTrue());

        condition = engine.getCondition("@false && true ^ false && false");
        assertEquals(false && true ^ false && false, condition.isTrue());

        condition = engine.getCondition("@false && true ^ false && true");
        assertEquals(false && true ^ false && true, condition.isTrue());

        condition = engine.getCondition("@false && true ^ true && false");
        assertEquals(false && true ^ true && false, condition.isTrue());

        condition = engine.getCondition("@false && true ^ true && true");
        assertEquals(false && true ^ true && true, condition.isTrue());

        condition = engine.getCondition("@true && false ^ false && false");
        assertEquals(true && false ^ false && false, condition.isTrue());

        condition = engine.getCondition("@true && false ^ false && true");
        assertEquals(true && false ^ false && true, condition.isTrue());

        condition = engine.getCondition("@true && false ^ true && false");
        assertEquals(true && false ^ true && false, condition.isTrue());

        condition = engine.getCondition("@true && false ^ true && true");
        assertEquals(true && false ^ true && true, condition.isTrue());

        condition = engine.getCondition("@true && true ^ false && false");
        assertEquals(true && true ^ false && false, condition.isTrue());

        condition = engine.getCondition("@true && true ^ false && false");
        assertEquals(true && true ^ false && false, condition.isTrue());

        condition = engine.getCondition("@true && true ^ false && true");
        assertEquals(true && true ^ false && true, condition.isTrue());

        condition = engine.getCondition("@true && true ^ true && false");
        assertEquals(true && true ^ true && false, condition.isTrue());

        condition = engine.getCondition("@true && true ^ true && true");
        assertEquals(true && true ^ true && true, condition.isTrue());

        condition = engine.getCondition("@false ^ false && false ^ false");
        assertEquals(false ^ false && false ^ false, condition.isTrue());

        condition = engine.getCondition("@false ^ false && false ^ true");
        assertEquals(false ^ false && false ^ true, condition.isTrue());

        condition = engine.getCondition("@false ^ false && true ^ false");
        assertEquals(false ^ false && true ^ false, condition.isTrue());

        condition = engine.getCondition("@false ^ false && true ^ true");
        assertEquals(false ^ false && true ^ true, condition.isTrue());

        condition = engine.getCondition("@false ^ true && false ^ false");
        assertEquals(false ^ true && false ^ false, condition.isTrue());

        condition = engine.getCondition("@false ^ true && false ^ false");
        assertEquals(false ^ true && false ^ false, condition.isTrue());

        condition = engine.getCondition("@false ^ true && false ^ true");
        assertEquals(false ^ true && false ^ true, condition.isTrue());

        condition = engine.getCondition("@false ^ true && true ^ false");
        assertEquals(false ^ true && true ^ false, condition.isTrue());

        condition = engine.getCondition("@false ^ true && true ^ true");
        assertEquals(false ^ true && true ^ true, condition.isTrue());

        condition = engine.getCondition("@true ^ false && false ^ false");
        assertEquals(true ^ false && false ^ false, condition.isTrue());

        condition = engine.getCondition("@true ^ false && false ^ true");
        assertEquals(true ^ false && false ^ true, condition.isTrue());

        condition = engine.getCondition("@true ^ false && true ^ false");
        assertEquals(true ^ false && true ^ false, condition.isTrue());

        condition = engine.getCondition("@true ^ false && true ^ true");
        assertEquals(true ^ false && true ^ true, condition.isTrue());

        condition = engine.getCondition("@true ^ true && false ^ false");
        assertEquals(true ^ true && false ^ false, condition.isTrue());

        condition = engine.getCondition("@true ^ true && false ^ false");
        assertEquals(true ^ true && false ^ false, condition.isTrue());

        condition = engine.getCondition("@true ^ true && false ^ true");
        assertEquals(true ^ true && false ^ true, condition.isTrue());

        condition = engine.getCondition("@true ^ true && true ^ false");
        assertEquals(true ^ true && true ^ false, condition.isTrue());

        condition = engine.getCondition("@true ^ true && true ^ true");
        assertEquals(true ^ true && true ^ true, condition.isTrue());
    }

    /**
     * Verifies that conditions read from a <tt>conditions.xml</tt> have the expected type.
     */
    @Test
    public void testReadConditionTypes()
    {
        DefaultContainer parent = new DefaultContainer();
        RulesEngine rules = new RulesEngineImpl(new ConditionContainer(parent), Platforms.MAC_OSX);
        parent.addComponent(RulesEngine.class, rules);
        IXMLParser parser = new XMLParser();
        IXMLElement conditions = parser.parse(getClass().getResourceAsStream("conditions.xml"));
        rules.analyzeXml(conditions);

        assertTrue(rules.getCondition("and1") instanceof AndCondition);
        assertTrue(rules.getCondition("not1") instanceof NotCondition);
        assertTrue(rules.getCondition("or1") instanceof OrCondition);
        assertTrue(rules.getCondition("xor1") instanceof XorCondition);
        assertTrue(rules.getCondition("variable1") instanceof VariableCondition);
        assertTrue(rules.getCondition("comparenumerics1") instanceof CompareNumericsCondition);
        assertTrue(rules.getCondition("compareversions1") instanceof CompareVersionsCondition);
        assertTrue(rules.getCondition("empty1") instanceof EmptyCondition);
        assertTrue(rules.getCondition("exists1") instanceof ExistsCondition);
        assertTrue(rules.getCondition("java1") instanceof JavaCondition);
        assertTrue(rules.getCondition("packselection1") instanceof PackSelectionCondition);
        assertTrue(rules.getCondition("ref1") instanceof RefCondition);
        assertTrue(rules.getCondition("user1") instanceof UserCondition);
    }

    /**
     * Verifies that the pre-defined platform conditions:
     * <ul>
     * <li>izpack.aixinstall
     * <li>izpack.windowsinstall
     * <li>izpack.windowsinstall.xp
     * <li>izpack.windowsinstall.2003
     * <li>izpack.windowsinstall.vista
     * <li>izpack.windowsinstall.7
     * <li>izpack.linuxinstall
     * <li>izpack.solarisinstall
     * <li>izpack.solarisinstall.x86
     * <li>izpack.solarisinstall.sparc
     * <li>izpack.macinstall
     * <li>izpack.macinstall.osx
     * </ul>
     * evaluate correctly for a range of platforms
     */
    @Test
    public void testPlatformConditions()
    {
        checkPlatformCondition(Platforms.AIX, AIX_INSTALL);
        checkPlatformCondition(Platforms.WINDOWS, WINDOWS_INSTALL);
        checkPlatformCondition(Platforms.WINDOWS_XP, WINDOWS_XP_INSTALL, WINDOWS_INSTALL);
        checkPlatformCondition(Platforms.WINDOWS_2003, WINDOWS_2003_INSTALL, WINDOWS_INSTALL);
        checkPlatformCondition(Platforms.WINDOWS_VISTA, WINDOWS_VISTA_INSTALL, WINDOWS_INSTALL);
        checkPlatformCondition(Platforms.WINDOWS_7, WINDOWS_7_INSTALL, WINDOWS_INSTALL);
        checkPlatformCondition(Platforms.LINUX, LINUX_INSTALL);
        checkPlatformCondition(Platforms.SUNOS, SOLARIS_INSTALL);
        checkPlatformCondition(Platforms.SUNOS_X86, SOLARIS_X86_INSTALL, SOLARIS_INSTALL);
        checkPlatformCondition(Platforms.SUNOS_SPARC, SOLARIS_SPARC_INSTALL, SOLARIS_INSTALL);
        checkPlatformCondition(Platforms.MAC, MAC_INSTALL);
        checkPlatformCondition(Platforms.MAC_OSX, MAC_OSX_INSTALL, MAC_INSTALL);
    }

    /**
     * Verifies that the specified conditions evaluate {@code true} for the specified platform.
     * <p/>
     * Platform conditions not specified will be evaluated to ensure they evaluate {@code false}
     *
     * @param platform   the 'current' platform
     * @param conditions the condition identifiers
     */
    private void checkPlatformCondition(Platform platform, String... conditions)
    {
        DefaultContainer parent = new DefaultContainer();
        RulesEngine rules = new RulesEngineImpl(new ConditionContainer(parent), platform);
        for (String condition : conditions)
        {
            assertTrue("Expected " + condition + " to be true", rules.isConditionTrue(condition));
        }
        List<String> falseConditions = new ArrayList<String>(Arrays.asList(INSTALL_CONDITIONS));
        falseConditions.removeAll(Arrays.asList(conditions));
        for (String falseCondition : falseConditions)
        {
            assertFalse("Expected " + falseCondition + " to be false", rules.isConditionTrue(falseCondition));
        }
    }
}

