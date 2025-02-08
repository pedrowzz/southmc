package wwz.pedro.vital.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import wwz.pedro.vital.essencial.Rank;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

  String name();

  String[] aliases() default {};

  String description() default "";

  String usage() default "";

  Rank[] groupsToUse() default {Rank.MEMBER};
}
