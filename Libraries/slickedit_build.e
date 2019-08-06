#include "slick.sh"

_command void slickedit_build()
{
   save_all();

   start_process( false, false, true, true );

   clear_pbuffer();

   clear_all_error_markers();

   _str command = "ant -noclasspath  -Dbasedir=" :+ get_env("UNDERCAMBER_PROJECT_ROOT") :+ " -buildfile " :+ get_env("UNDERCAMBER_PROJECT_ROOT") :+ FILESEP :+ "build.xml";

   message( command );

   concur_command( command, false, true, false, true );
}
