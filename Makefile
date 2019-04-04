# maven vars:
mvn=mvn
test=true
mvn_flags=

# set maven test flags depending on value of "test" variable
ifneq ($(test), true)
 $(info Not performing tests!)
 mvn_test_flags = -Dmaven.test.skip=true
endif

mvn_goals_with_flags=clean install $(mvn_flags) $(mvn_test_flags)

all:
	$(mvn) $(mvn_goals_with_flags)