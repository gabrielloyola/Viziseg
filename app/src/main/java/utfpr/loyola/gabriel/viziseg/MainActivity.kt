package utfpr.loyola.gabriel.viziseg

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_new_group.*
import utfpr.loyola.gabriel.viziseg.fragment.GroupsFragment
import utfpr.loyola.gabriel.viziseg.fragment.MyAccountFragment
import utfpr.loyola.gabriel.viziseg.fragment.PeopleFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(GroupsFragment())

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_contacts -> {
                    replaceFragment(PeopleFragment())
                    true
                }
                R.id.navigation_groups -> {
                    replaceFragment(GroupsFragment())
                    true
                }
                R.id.navigation_profile -> {
                    replaceFragment(MyAccountFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, fragment)
            .commit()
    }
}
