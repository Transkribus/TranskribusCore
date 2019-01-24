# maven vars:
mvn=mvn
tests=true
mvn_flags=
# set maven test flags depending on value of "tests" variable
ifeq ($(tests), true)
 $(info Performing tests!)
 mvn_test_flags =
else
 $(info Skipping tests!)
 mvn_test_flags = -Dmaven.test.skip=true
endif

mvn_goals_with_flags=clean install $(mvn_flags) $(mvn_test_flags)

all:
	$(mvn) $(mvn_goals_with_flags)