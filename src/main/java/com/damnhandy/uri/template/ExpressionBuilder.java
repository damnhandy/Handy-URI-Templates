/*
 *
 *
 */
package com.damnhandy.uri.template;

import java.util.LinkedList;
import java.util.List;

import com.damnhandy.uri.template.impl.ExpressionImpl;
import com.damnhandy.uri.template.impl.Modifier;
import com.damnhandy.uri.template.impl.Operator;
import com.damnhandy.uri.template.impl.VarSpec;

/**
 * A ExpressionBuilder.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class ExpressionBuilder
{
   /**
    * 
    */
   private Operator operator;

   /**
    * 
    */
   private List<VarSpec> varSpecs = new LinkedList<VarSpec>();

   /**
    * 
    * Create a new ExpressionBuilder.
    * 
    * @param operator
    */
   private ExpressionBuilder(Operator operator)
   {
      this.operator = operator;
   }

   static ExpressionBuilder create(Operator operator)
   {
      return new ExpressionBuilder(operator);
   }

   /**
    * FIXME Comment this
    * 
    * @param varName
    * @return
    */
   public ExpressionBuilder var(String varName)
   {
      return var(varName, Modifier.NONE, null);
   }

   /**
    * FIXME Comment this
    * 
    * @param varName
    * @param explode
    * @return
    */
   public ExpressionBuilder var(String varName, boolean explode)
   {
      if (explode)
      {
         return var(varName, Modifier.EXPLODE, null);
      }
      return var(varName, Modifier.NONE, null);
   }

   /**
    * FIXME Comment this
    * 
    * @param varName
    * @param prefix
    * @return
    */
   public ExpressionBuilder var(String varName, int prefix)
   {
      return var(varName, Modifier.PREFIX, prefix);
   }

   /**
    * FIXME Comment this
    * 
    * @param varName
    * @param modifier
    * @param position
    * @return
    */
   private ExpressionBuilder var(String varName, Modifier modifier, Integer position)
   {
      varSpecs.add(new VarSpec(varName, modifier, position));
      return this;
   }

   public Expression build() 
   {
      return new ExpressionImpl(null, operator, varSpecs);
   }
}
