package groovy.org.apache.cloudstack.ldap

import com.cloud.utils.Pair
import org.apache.cloudstack.api.command.LdapListConfigurationCmd
import org.apache.cloudstack.api.response.LdapConfigurationResponse
import org.apache.cloudstack.ldap.LdapConfigurationVO
import org.apache.cloudstack.ldap.LdapManager

class LdapListConfigurationCmdSpec extends spock.lang.Specification {

    def "Test failed response from execute"() {
        given: "We have an LdapManager and a LdapListConfigurationsCmd"
        def ldapManager = Mock(LdapManager)
        List<LdapConfigurationVO> ldapConfigurationList = new ArrayList()
        Pair<List<LdapConfigurationVO>, Integer> ldapConfigurations = new Pair<List<LdapConfigurationVO>, Integer>();
        ldapConfigurations.set(ldapConfigurationList, ldapConfigurationList.size())
        ldapManager.listConfigurations(_) >> ldapConfigurations
        def ldapListConfigurationCmd = new LdapListConfigurationCmd(ldapManager)
        when: "LdapListConfigurationCmd is executed"
        ldapListConfigurationCmd.execute()
        then: "Its response object contains an array that is 0"
        ldapListConfigurationCmd.getResponseObject().getResponses().size() == 0
    }

    def "Test getEntityOwnerId is 1"() {
        given: "We have an LdapManager and ListLdapConfigurationCmd"
        def ldapManager = Mock(LdapManager)
        def ldapListConfigurationCmd = new LdapListConfigurationCmd(ldapManager)
        when: "Get entity owner id is called"
        long ownerId = ldapListConfigurationCmd.getEntityOwnerId()
        then: "a 1 is returned"
        ownerId == 1
    }

    def "Test successful response from execute"() {
        given: "We have an LdapManager with a configuration and a LdapListConfigurationsCmd"
        def ldapManager = Mock(LdapManager)
        List<LdapConfigurationVO> ldapConfigurationList = new ArrayList()
        ldapConfigurationList.add(new LdapConfigurationVO("localhost", 389))
        Pair<List<LdapConfigurationVO>, Integer> ldapConfigurations = new Pair<List<LdapConfigurationVO>, Integer>();
        ldapConfigurations.set(ldapConfigurationList, ldapConfigurationList.size())
        ldapManager.listConfigurations(_) >> ldapConfigurations
        ldapManager.createLdapConfigurationResponse(_) >> new LdapConfigurationResponse("localhost", 389)
        def ldapListConfigurationCmd = new LdapListConfigurationCmd(ldapManager)
        when: "LdapListConfigurationsCmd is executed"
        ldapListConfigurationCmd.execute()
        then: "Its response object contains an array that is not 0 in size"
        ldapListConfigurationCmd.getResponseObject().getResponses().size() != 0
    }

    def "Test successful return of getCommandName"() {
        given: "We have an LdapManager and LdapListConfigurationCmd"
        def ldapManager = Mock(LdapManager)
        def ldapListConfigurationCmd = new LdapListConfigurationCmd(ldapManager)
        when: "Get command name is called"
        String commandName = ldapListConfigurationCmd.getCommandName()
        then: "ldapconfigurationresponse is returned"
        commandName == "ldapconfigurationresponse"
    }

    def "Test successful setting of hostname"() {
        given: "We have an LdapManager and LdapListConfigurationCmd"
        def ldapManager = Mock(LdapManager)
        def ldapListConfigurationCmd = new LdapListConfigurationCmd(ldapManager)
        when: "The hostname is set"
        ldapListConfigurationCmd.setHostname("localhost")
        then: "Get hostname returns the set value"
        ldapListConfigurationCmd.getHostname() == "localhost"
    }

    def "Test successful setting of Port"() {
        given: "We have an LdapManager and LdapListConfigurationCmd"
        def ldapManager = Mock(LdapManager)
        def ldapListConfigurationCmd = new LdapListConfigurationCmd(ldapManager)
        when: "The port is set"
        ldapListConfigurationCmd.setPort(389)
        then: "Get port returns the set value"
        ldapListConfigurationCmd.getPort() == 389
    }
}
